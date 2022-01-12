package com.example.chat_data.data.usecase

import com.example.auth.User
import com.example.auth.datasource.FirebaseDataSourceImpl
import com.example.auth.datasource.MeLocalDataSource
import com.example.auth.datasource.UserDataSource
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepositoryImpl
import com.example.auth_feature.data.FakeFirebaseDataSource
import com.example.auth_feature.data.FakeMeDataSource
import com.example.auth_feature.data.FakeUserDataSource
import com.example.chat_data.Chat
import com.example.chat_data.data.FakeChatDataSource
import com.example.chat_data.data.FakeRemoteChatHelper
import com.example.chat_data.datasource.ChatDatasource
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.repo.ChatRepository
import com.example.chat_data.repo.IRemoteChatHelper
import com.example.chat_data.repo.RemoteChatHelper
import com.example.chat_data.usecase.ReceiveChatUseCase
import com.example.chat_data.usecase.RetryUnSendChats
import com.example.chat_data.usecase.SendChatUseCase
import com.example.pojo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UsecaseTests {

    private val meUser = User(userName = "kshitij", mobileNumber = 9953319602)
    private val user2 = User(userName = "harshit", mobileNumber = 7765543256)
    private val user3 = User(userName = "Han solo", mobileNumber = 9765543256)

    private val remoteList = mutableListOf(meUser, user2, user3)
    private val localUser = mutableListOf<User>()

    lateinit var meUserDataSource: UserDataSource
    lateinit var remoteUseDataSource: FakeUserDataSource
    lateinit var roomDataSource: UserDataSource

    lateinit var chatDataSource: FakeChatDataSource
    lateinit var remoteChatHelper: FakeRemoteChatHelper

    lateinit var chatRepo: ChatRepository
    lateinit var userRepositoryImpl: UserRepositoryImpl

    lateinit var sendChatUseCase: SendChatUseCase
    lateinit var receiveChatUseCase: ReceiveChatUseCase
    lateinit var retryUnSendChats: RetryUnSendChats


    @Before
    fun before() {
        meUserDataSource = FakeMeDataSource().apply { user = meUser }
        remoteUseDataSource = FakeUserDataSource(remoteList)
        roomDataSource = FakeUserDataSource(localUser)

        chatDataSource = FakeChatDataSource(mutableListOf())
        remoteChatHelper = FakeRemoteChatHelper()

        chatRepo = ChatRepository(remoteChatHelper, chatDataSource)
        userRepositoryImpl = UserRepositoryImpl(
            userRoomDataSource = roomDataSource,
            userFirebaseDataSource = remoteUseDataSource,
            meDataSource = meUserDataSource
        )
        val firebaseDataRepository = FirebaseDataRepository(FakeFirebaseDataSource())

        sendChatUseCase = SendChatUseCase(userRepositoryImpl, chatRepo, firebaseDataRepository)
        receiveChatUseCase = ReceiveChatUseCase(userRepositoryImpl, chatRepo)
        retryUnSendChats = RetryUnSendChats(userRepositoryImpl, chatRepo, firebaseDataRepository)
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
            success = false, type = ChatType.SENT
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
        val chat = Chat(
            chatId = 6900,
            message = "Test message",
            userId = user2.userName,
            timeStamp = System.currentTimeMillis(),
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
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                timeStamp = System.currentTimeMillis(),
                success = false, type = ChatType.SENT
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
            Chat(
                message = "Test message $it",
                userId = user2.userName,
                timeStamp = System.currentTimeMillis(),
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
}