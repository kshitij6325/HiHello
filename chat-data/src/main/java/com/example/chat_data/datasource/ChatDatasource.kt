package com.example.chat_data.datasource

import com.example.chat_data.Chat
import com.example.pojo.Result

interface ChatDatasource {
    suspend fun addChats(chat: Chat): Result<Long>
    suspend fun getAllUserChat(userId: String): Result<List<Chat>>
    suspend fun getChat(chatId: String): Result<Chat>
    suspend fun updateChat(chat: Chat): Result<Boolean>
    suspend fun updateChatSuccessState(chatId: String, success: Boolean): Result<Boolean>
    suspend fun deleteChat(chatId: String): Result<Boolean>
    suspend fun deleteAllUserChat(userId: String): Result<Boolean>
    suspend fun getAllUnSendChat(): Result<List<Chat>>
}