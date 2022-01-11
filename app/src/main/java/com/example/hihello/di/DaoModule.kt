package com.example.hihello.di

import android.app.Application
import androidx.room.Room
import com.example.hihello.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun getAppDatabase(application: Application) = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "hihello"
    ).build()

    @Provides
    fun getUserDao(appDatabase: AppDatabase) = appDatabase.getUserDao()

    @Provides
    fun getChatDao(appDatabase: AppDatabase) = appDatabase.getChatDao()
}