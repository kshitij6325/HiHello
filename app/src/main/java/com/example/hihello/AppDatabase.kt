package com.example.hihello

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.auth.User
import com.example.auth.room.UserDao

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}