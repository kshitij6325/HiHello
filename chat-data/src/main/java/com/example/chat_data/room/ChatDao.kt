package com.example.chat_data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.auth.User
import com.example.chat_data.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("select * from chats")
    suspend fun getAllChats(): List<Chat>

    @Query("select * from chats where user_id = :userId order by time_stamp desc limit :limit")
    suspend fun getAllUserChats(userId: String, limit: Int): List<Chat>

    @Query("select * from chats where user_id=:userId")
    fun getAllUserChatLiveData(userId: String): LiveData<List<Chat>>

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

    @Query("select * from users join chats on user_id = user_name group by user_name order by time_stamp desc")
    fun getAllUserChats(): Flow<Map<User, List<Chat>>>
}