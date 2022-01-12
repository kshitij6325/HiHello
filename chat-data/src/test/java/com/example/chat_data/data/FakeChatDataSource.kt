package com.example.chat_data.data

import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatDatasource
import com.example.pojo.Result
import java.lang.Exception

class FakeChatDataSource(private val mutableChatList: MutableList<Chat>) : ChatDatasource {

    var error = false

    override suspend fun addChats(chat: Chat): Result<Long> {
        return if (error) Result.Failure(Exception("Issue while adding chat.."))
        else {
            val index = mutableChatList.size
            mutableChatList.add(chat.copy(chatId = index.toLong()))
            Result.Success(index.toLong())
        }
    }

    override suspend fun getAllUserChat(userId: String): Result<List<Chat>> {
        return if (error) Result.Failure(Exception("Issue while fetching chat.."))
        else {
            val chatList = mutableChatList.filter { it.userId == userId }
            Result.Success(chatList)
        }
    }

    override suspend fun getChat(chatId: String): Result<Chat> {
        return if (error) Result.Failure(Exception("Issue while fetching chat.."))
        else {
            mutableChatList.find { it.chatId.toString() == chatId }?.let { Result.Success(it) }
                ?: Result.Failure(Exception("Issue while fetching chat.."))

        }
    }

    override suspend fun updateChat(chat: Chat): Result<Boolean> {
        return if (error) Result.Failure(Exception("Issue while fetching chat.."))
        else {
            val chatIndex = mutableChatList.indexOf(chat)
            if (chatIndex == -1) return Result.Failure(Exception("Issue while fetching chat.."))
            mutableChatList.add(chatIndex, chat)
            return Result.Success(true)
        }
    }

    override suspend fun updateChatSuccessState(chatId: String, success: Boolean): Result<Boolean> {
        return if (error) Result.Failure(Exception("Issue while fetching chat.."))
        else {
            val chatIndex = mutableChatList.indexOfFirst {
                it.chatId == chatId.toLong()
            }
            if (chatIndex == -1) return Result.Failure(Exception("Issue while fetching chat.."))
            val chat = mutableChatList[chatIndex]
            mutableChatList[chatIndex] = chat.copy(success = success)
            return Result.Success(true)
        }
    }

    override suspend fun deleteChat(chatId: String): Result<Boolean> {
        if (error) return Result.Failure(Exception("Issue while fetching chat.."))
        val chatIndex = mutableChatList.indexOfFirst {
            it.chatId == chatId.toLong()
        }
        if (chatIndex == -1) return Result.Failure(Exception("Issue while fetching chat.."))
        mutableChatList.removeAt(chatIndex)
        return Result.Success(true)
    }

    override suspend fun deleteAllUserChat(userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUnSendChat(): Result<List<Chat>> {
        if (error) return Result.Failure(Exception("Issue while fetching chat.."))
        val chatList = mutableChatList.filter { !it.success }
        return Result.Success(chatList)
    }
}