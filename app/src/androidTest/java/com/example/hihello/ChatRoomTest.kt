package com.example.hihello

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.room.ChatDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatRoomTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    lateinit var chatDao: ChatDao

    val chat1 = Chat(
        message = "Test message",
        userId = "userId",
        timeStamp = System.currentTimeMillis(),
        success = false, type = ChatType.SENT
    )

    val chat2 = Chat(
        message = "Test message 2",
        userId = "userId",
        timeStamp = System.currentTimeMillis(),
        success = false, type = ChatType.SENT
    )

    @Before
    fun initDatabaseAndDao() {
        val dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        chatDao = dataBase.getChatDao()
    }

    @Test
    fun addChat_success() = runTest {
        val chatRes = chatDao.addChat(chat1)
        val chatRes2 = chatDao.addChat(chat2)

        assert(chatRes == 1L && chatRes2 == 2L)
    }

    @Test
    fun addChat_bulk_success() = runTest {

        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1)
            assert(chatRes == i.toLong())
        }

        val allChat = chatDao.getAllChats()
        assert(allChat.size == 50)
    }

    @Test
    fun getChatByUser_success() = runTest {

        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1)
            assert(chatRes == i.toLong())
        }

        val allChat = chatDao.getAllUserChats("userId")
        assert(allChat.size == 50)
    }

    @Test
    fun getChatByUser_fail() = runTest {

        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1)
            assert(chatRes == i.toLong())
        }

        val allChat = chatDao.getAllUserChats("user2")
        assert(allChat.isEmpty())
    }

    @Test
    fun getChatBySuccess_success() = runTest {

        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1.copy(success = i % 2 == 0))
            assert(chatRes == i.toLong())
        }

        val allChat = chatDao.getAllUnSendChats()
        assert(allChat.size == 25)
    }

    @Test
    fun updateChatSuccess_success() = runTest {

        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1.copy(success = i % 2 == 0))
            assert(chatRes == i.toLong())
        }
        for (i in 1..50) {
            val res = chatDao.updateChatSuccessState(chatId = i.toString(), true)
            assert(res == 1)
        }

        val list = chatDao.getAllUnSendChats()
        assert(list.isEmpty())
    }

    @Test
    fun getChatList_liveDataTest() = runTest {
        var newValueUpdated = false
        for (i in 1..50) {
            val chatRes = chatDao.addChat(chat1.copy(success = true))
            assert(chatRes == i.toLong())
        }

        chatDao.getAllUserChatLiveData("userId").observeForever {
            if (newValueUpdated) {
                assert(it.size == 51)
            } else {
                assert(it.size == 50)
            }
        }

        newValueUpdated = true
        chatDao.addChat(chat2)

    }
}