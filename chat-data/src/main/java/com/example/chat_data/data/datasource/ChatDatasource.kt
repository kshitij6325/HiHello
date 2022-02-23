package com.example.chat_data.data.datasource

import com.example.auth.data.User
import com.example.chat_data.data.Chat
import com.example.pojo.Result
import kotlinx.coroutines.flow.Flow

interface ChatDatasource {
    suspend fun addChats(chat: Chat): Result<Long>
    suspend fun getAllUserChat(userId: String, limit: Int, offset: Int): Result<List<Chat>>
    suspend fun getChat(chatId: String): Result<Chat>
    suspend fun updateChat(chat: Chat): Result<Boolean>
    suspend fun updateChatSuccessState(chatId: String, success: Boolean): Result<Boolean>
    suspend fun deleteChat(chatId: String): Result<Boolean>
    suspend fun deleteAllUserChat(userId: String): Result<Boolean>
    suspend fun getAllUnSendChat(): Result<List<Chat>>
    fun getLatestUserChatFlow(userId: String): Flow<Chat>
    fun getAllUserChatsMap(): Flow<Map<User, List<Chat>>>
}