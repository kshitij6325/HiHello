package com.example.chat_data.data.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.data.User
import com.example.auth.data.datasource.UserDataSource
import com.example.auth.data.repo.FirebaseDataRepository
import com.example.auth.data.repo.UserRepositoryImpl
import com.example.auth_feature.data.FakeFirebaseDataSource
import com.example.auth_feature.data.FakeMeDataSource
import com.example.auth_feature.data.FakeUserDataSource
import com.example.chat_data.data.Chat
import com.example.chat_data.data.FakeChatDataSource
import com.example.chat_data.data.FakeMediaSource
import com.example.chat_data.data.FakeRemoteChatHelper
import com.example.chat_data.data.datasource.ChatMedia
import com.example.chat_data.data.datasource.ChatType
import com.example.chat_data.data.datasource.getChatDate
import com.example.chat_data.data.repo.ChatRepository
import com.example.chat_data.domain.*
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import com.example.pojo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UsecaseTests {

    private val meUser = User(userName = "kshitij", mobileNumber = 9953319602)
    private val user2 = User(userName = "harshit", mobileNumber = 7765543256)
    private val user3 = User(userName = "Han solo", mobileNumber = 9765543256)

    private val remoteList = mutableListOf(meUser, user2, user3)
    private val localUser = mutableListOf<User>()

    private val mediaMap =
        mutableMapOf<String, MediaSource>()
    private val chatList = mutableListOf<Chat>()

    lateinit var meUserDataSource: UserDataSource
    lateinit var remoteUseDataSource: FakeUserDataSource
    lateinit var roomDataSource: UserDataSource

    lateinit var chatDataSource: FakeChatDataSource
    lateinit var remoteChatHelper: FakeRemoteChatHelper
    lateinit var mediaDataSource: FakeMediaSource

    lateinit var chatRepo: ChatRepository
    lateinit var userRepositoryImpl: UserRepositoryImpl

    lateinit var sendChatUseCase: SendChatUseCase
    lateinit var receiveChatUseCase: ReceiveChatUseCase
    lateinit var retryUnSendChats: RetryUnSendChats
    lateinit var getAllUserChatUseCase: GetAllUserChatUseCase
    lateinit var getAllChatUserCase: GetAllChatsUseCase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun before() {
        meUserDataSource = FakeMeDataSource().apply { user = meUser }
        remoteUseDataSource = FakeUserDataSource(remoteList)
        roomDataSource = FakeUserDataSource(localUser)

        chatDataSource = FakeChatDataSource(chatList)
        remoteChatHelper = FakeRemoteChatHelper()
        mediaDataSource = FakeMediaSource(mediaMap)

        chatRepo = ChatRepository(remoteChatHelper, chatDataSource)
        userRepositoryImpl = UserRepositoryImpl(
            userRoomDataSource = roomDataSource,
            userFirebaseDataSource = remoteUseDataSource,
            meDataSource = meUserDataSource
        )
        val firebaseDataRepository = FirebaseDataRepository(FakeFirebaseDataSource())
        val mediaRepo = MediaRepository(mediaDataSource)

        sendChatUseCase =
            SendChatUseCase(userRepositoryImpl, chatRepo, firebaseDataRepository, mediaRepo)
        receiveChatUseCase = ReceiveChatUseCase(userRepositoryImpl, chatRepo)
        retryUnSendChats =
            RetryUnSendChats(userRepositoryImpl, chatRepo, firebaseDataRepository, mediaRepo)
        getAllUserChatUseCase = GetAllUserChatUseCase(chatRepository = chatRepo)
        getAllChatUserCase = GetAllChatsUseCase(userRepository = userRepositoryImpl, chatRepo)
    }

    @Test
    fun sendChatUseCase_success() = runTest {
        sendChatUseCase.apply {
            onSuccess = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat data is correct
                assert(chatRes is Result.Success && chatRes.data.success)
                assert(chatRes is Result.Success && chatRes.data.userId == user2.userName)
            }
            onFailure = {
                assert(false)
            }
        }.invoke("Henlo", user2.userName)
    }

    @Test
    fun sendChatUseCase_networkErrorInChatHelper() = runTest {
        remoteChatHelper.error = true
        sendChatUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat data is correct
                assert(chatRes is Result.Success && !chatRes.data.success)
                assert(chatRes is Result.Success && chatRes.data.userId == user2.userName)
                assert(chatRes is Result.Success && chatRes.data.message == "Henlo")
            }
        }.invoke("Henlo", user2.userName)
    }

    @Test
    fun sendChatUseCase_networkErrorInUerRemoteDS() = runTest {
        remoteUseDataSource.fail = true
        sendChatUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is not created
                assert(userRes is Result.Failure)

                //checking if chat is not created
                assert(chatRes is Result.Failure)
            }
        }.invoke("Henlo", user2.userName)
    }

    @Test
    fun sendChatUseCase_dbErrorSavingChat() = runTest {
        chatDataSource.error = true
        sendChatUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat is not created
                assert(chatRes is Result.Failure)
            }
        }.invoke("Henlo", user2.userName)
    }

    @Test
    fun receiveChatUseCase_success() = runTest {
        val chat = Chat(
            chatId = 0,
            message = "Test message",
            userId = user2.userName,
            timeStamp = System.currentTimeMillis(),
            success = false, type = ChatType.SENT,
            date = System.currentTimeMillis().getChatDate()
        )
        val jsonString = Json.encodeToString(chat)
        receiveChatUseCase.apply {
            onSuccess = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat is not created
                assert(chatRes is Result.Success && chatRes.data.type == ChatType.RECEIVED && chatRes.data.success)
            }
            onFailure = {
                assert(false)
            }
        }.invoke(jsonString)
    }

    @Test
    fun receiveChatUseCase_FailureChatDberror() = runTest {
        chatDataSource.error = true
        val milli = System.currentTimeMillis()
        val chat = Chat(
            chatId = 6900,
            message = "Test message",
            userId = user2.userName,
            timeStamp = milli,
            date = milli.getChatDate(),
            success = false, type = ChatType.SENT
        )
        val jsonString = Json.encodeToString(chat)
        receiveChatUseCase.apply {
            onSuccess = {
                assert(false)
            }
            onFailure = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat is not created
                assert(chatRes is Result.Failure)
            }
        }.invoke(jsonString)
    }

    @Test
    fun retrySend_success() = runTest {
        (1..10).map {
            val milli = System.currentTimeMillis()
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                success = false, type = ChatType.SENT,
                timeStamp = milli,
                date = milli.getChatDate(),
            )
        }.forEach { chatRepo.addChat(it) }

        //checking unsend chats count before running use case
        var unSendChats = chatRepo.getAllUnSendChats()
        assert(unSendChats is Result.Success && unSendChats.data.size == 10)

        retryUnSendChats.apply {
            onSuccess = {
                assert(it.isEmpty())
                unSendChats = chatRepo.getAllUnSendChats()
                assert(unSendChats is Result.Success && (unSendChats as Result.Success).data.isEmpty())
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }

    @Test
    fun retrySend_NetworkError_SendingChat() = runTest {
        remoteChatHelper.error = true
        (1..10).map {
            val milli = System.currentTimeMillis()
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                timeStamp = milli,
                date = milli.getChatDate(),
                success = false, type = ChatType.SENT
            )
        }.forEach { chatRepo.addChat(it) }

        //checking unsend chats count before running use case
        var unSendChats = chatRepo.getAllUnSendChats()
        assert(unSendChats is Result.Success && unSendChats.data.size == 10)

        retryUnSendChats.apply {
            onSuccess = {
                assert(it.isNotEmpty())
                unSendChats = chatRepo.getAllUnSendChats()
                assert(unSendChats is Result.Success && (unSendChats as Result.Success).data.size == 10)
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }

    @Test
    fun getAllUsersChatLiveData_success() = runTest {
        (1..10).map {
            val milli = System.currentTimeMillis()
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                success = true, type = ChatType.SENT,
                timeStamp = milli,
                date = milli.getChatDate(),


                )
        }.forEach { chatRepo.addChat(it) }

        getAllUserChatUseCase.getUserChat(user2.userName, 2000, 0).run {
            assert(this is Result.Success && data.size == 10)
        }
    }

    @Test
    fun getAllUsersChatLiveData_success_empty() = runTest {
        (1..10).map {
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                timeStamp = System.currentTimeMillis(),
                success = true, type = ChatType.SENT,
                date = System.currentTimeMillis().getChatDate()
            )
        }.forEach { chatRepo.addChat(it) }

        getAllUserChatUseCase.getUserChat("user2", 1000, 0).run {
            assert(this is Result.Success && this.data.isEmpty())
        }
    }

    @Test
    fun sendChatWithMediaUseCase_success() = runTest {
        val file = kotlin.io.path.createTempFile("temp", "png").toFile()
        val mediSource = MediaSource.File(file, MediaType.IMAGE)
        sendChatUseCase.apply {
            onSuccess = {
                val chatRes = chatRepo.getChat(0)
                val userRes = userRepositoryImpl.getLocalUser(user2.userName)

                //checking if new user is created
                assert(userRes is Result.Success && userRes.data.userName == user2.userName)

                //checking if chat data is correct
                assert(chatRes is Result.Success && chatRes.data.success)
                assert(chatRes is Result.Success && chatRes.data.userId == user2.userName)
            }
            onFailure = {
                assert(false)
            }
        }.invoke("Henlo", user2.userName, mediSource)
        val mediaUrl = MediaSource.Url(mediSource.toString(), MediaType.IMAGE)
        val mediaRes = mediaDataSource.getMedia(
            mediaUrl, "type", MediaType.IMAGE
        ) {}

        //check if media is successfully created
        assert(mediaRes is Result.Success)

        //check if chat has correct media data
        val chat = chatRepo.getChat(0)
        assert(chat is Result.Success && chat.data.media?.url == mediSource.toString())
    }

    @Test
    fun retrySendWithMedia_success() = runTest {
        val file = kotlin.io.path.createTempFile("temp", "png").toFile()
        (1..10).map {
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                timeStamp = System.currentTimeMillis(),
                media = ChatMedia(localPath = file.absolutePath, type = MediaType.IMAGE),
                success = false, type = ChatType.SENT,
                date = System.currentTimeMillis().getChatDate()
            )
        }.forEach { chatRepo.addChat(it) }

        //checking unsend chats count before running use case
        var unSendChats = chatRepo.getAllUnSendChats()
        assert(unSendChats is Result.Success && unSendChats.data.size == 10)

        retryUnSendChats.apply {
            onSuccess = {
                assert(it.isEmpty())
                unSendChats = chatRepo.getAllUnSendChats()
                assert(unSendChats is Result.Success && (unSendChats as Result.Success).data.isEmpty())
            }
            onFailure = {
                assert(false)
            }
        }.invoke()

        //check if chat has correct media data

        val res = chatRepo.getAllUserChat(user2.userName, 2000, 0)
        assert(res is Result.Success)

        val chatList = (res as Result.Success).data
        for (chat in chatList) {
            assert(!chat.media?.url.isNullOrEmpty())
        }

    }

    /*@Test
    fun getAllChats_success() = runTest {

        userRepositoryImpl.createLocalUser(user2)
        userRepositoryImpl.createLocalUser(user3)

        (1..10).map {
            Chat(
                message = "Test message $it",
                userId = if (it % 2 == 0) user2.userName else user3.userName,
                timeStamp = System.currentTimeMillis(),
                success = true, type = ChatType.SENT
            )
        }.forEach { chatRepo.addChat(it) }

        getAllChatUserCase.get().collect {
            assert(it.size == 2)
        }
    }

    @Test
    fun getAllChats_noChats() = runTest {

        userRepositoryImpl.createLocalUser(user2)
        userRepositoryImpl.createLocalUser(user3)

        getAllChatUserCase.apply {
            onSuccess = {
                assert(it.isEmpty())
            }
            onFailure = {
                assert(false)
            }
        }.invoke()
    }*/
}