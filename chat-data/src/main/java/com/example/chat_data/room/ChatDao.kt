package com.example.chat_data.room

import androidx.room.*
import com.example.auth.User
import com.example.chat_data.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("select * from chats")
    suspend fun getAllChats(): List<Chat>

    @Query("select * from chats where user_id = :userId order by time_stamp desc limit :limit offset :offset")
    suspend fun getAllUserChats(userId: String, limit: Int, offset: Int): List<Chat>

    @Query("select * from chats where user_id=:userId order by time_stamp desc limit 1")
    fun getAllUserChatLiveData(userId: String): Flow<Chat>

    @Query("select * from chats where chat_id=:chatId")
    suspend fun getChat(chatId: String): Chat

    @Insert
    suspend fun addChat(chat: Chat): Long

    @Update
    suspend fun updateChat(chat: Chat)

    @Query("update chats set success = :success where chat_id=:chatId")
    suspend fun updateChatSuccessState(chatId: String, success: Boolean): Int

    @Query("delete from chats where chat_id in (:chatId)")
    suspend fun deleteChats(vararg chatId: String)

    @Query("delete from chats where user_id in (:userId)")
    suspend fun deleteUserChats(vararg userId: String)

    @Query("select * from chats where success = 0")
    suspend fun getAllUnSendChats(): List<Chat>

    @Query("select * from users join (select * , max(time_stamp) from chats group by user_id) on user_id = user_name")
    fun getAllUserChats(): Flow<Map<User, List<Chat>>>
}