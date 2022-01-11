package com.example.hihello

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.auth.User
import com.example.auth.room.UserDao
import com.example.chat_data.Chat
import com.example.chat_data.room.ChatDao

@Database(entities = [User::class, Chat::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao

    abstract fun getChatDao(): ChatDao
}