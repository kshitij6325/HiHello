package com.example.chat_data.room

import androidx.room.*
import com.example.chat_data.Chat

@Dao
interface ChatDao {

    @Query("select * from chats where user_id=:userId")
    suspend fun getAllUserChats(userId: String): List<Chat>

    @Query("select * from chats where chat_id=:chatId")
    suspend fun getChat(chatId: String): Chat

    @Insert
    suspend fun addChat(chat: Chat): Long

    @Update
    suspend fun updateChat(chat: Chat)

    @Query("update chats set success = :success where chat_id=:chatId")
    suspend fun updateChatSuccessState(chatId: String, success: Boolean)

    @Query("delete from chats where chat_id in (:chatId)")
    suspend fun deleteChats(vararg chatId: String)

    @Query("delete from chats where user_id in (:userId)")
    suspend fun deleteUserChats(vararg userId: String)

    @Query("select * from chats where success = 0")
    suspend fun getAllUnSendChats(): List<Chat>
}