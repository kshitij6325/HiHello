package com.example.chat_data.data.repo

import com.example.auth.User
import com.example.chat_data.Chat
import com.example.chat_data.data.FakeChatDataSource
import com.example.chat_data.data.FakeRemoteChatHelper
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.repo.ChatRepository
import com.example.pojo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatRepoTest {

    private val meUser = User(userName = "kshitij", mobileNumber = 9953319602)
    private val user2 = User(userName = "harshit", mobileNumber = 7765543256)
    private val user3 = User(userName = "Han solo", mobileNumber = 9765543256)

    private val remoteList = mutableListOf(meUser, user2, user3)
    private val localUser = mutableListOf<User>()

    lateinit var chatRepo: ChatRepository

    @Before
    fun runBefore() {
        val chatDatasource = FakeChatDataSource(mutableListOf())
        val remoteChatHelper = FakeRemoteChatHelper()
        chatRepo = ChatRepository(remoteChatHelper, chatDatasource)

    }


    @Test
    fun sendChat_successTest() = runTest {
        var chat =
            Chat(
                userId = user2.userName,
                type = ChatType.SENT,
                message = "Hiii hello",
                timeStamp = System.currentTimeMillis(),
                success = false
            )
        //adding chat
        val addRes = chatRepo.addChat(chat)
        assert(addRes is Result.Success && addRes.data == 0L)

        var getChatRes = chatRepo.getChat((addRes as Result.Success).data)
        assert(getChatRes is Result.Success && !getChatRes.data.success)

        chat = (getChatRes as Result.Success).data

        val res = chatRepo.sendChat(user2, "sectert", chat).map {
            chatRepo.updateChatSuccess(chat = chat.chatId.toString(), true)
        }
        getChatRes = chatRepo.getChat(0)

        assert(res is Result.Success)
        assert(getChatRes is Result.Success && getChatRes.data.success)
    }
}