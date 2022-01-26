package com.example.chat_data.di

import com.example.media_data.FirebaseStorageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ChatMediaModule {

    @Provides
    fun getSource(): FirebaseStorageDataSource.Companion.Source =
        FirebaseStorageDataSource.Companion.Source.CHAT
}