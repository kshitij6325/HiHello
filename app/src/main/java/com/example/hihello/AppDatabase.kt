package com.example.hihello

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.auth.data.User
import com.example.auth.app.room.UserDao
import com.example.chat_data.data.Chat
import com.example.chat_data.app.room.ChatDao

@Database(entities = [User::class, Chat::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao

    abstract fun getChatDao(): ChatDao
}