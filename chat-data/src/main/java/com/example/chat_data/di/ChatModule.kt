package com.example.chat_data.di

import com.example.chat_data.datasource.ChatDatasource
import com.example.chat_data.datasource.ChatRoomDatasource
import com.example.chat_data.repo.IRemoteChatHelper
import com.example.chat_data.repo.RemoteChatHelper
import com.example.media_data.FirebaseStorageDataSource
import com.example.media_data.MediaDataSource
import com.example.media_data.MediaSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {

    @Binds
    abstract fun getChatDataSource(chatRoomDatasource: ChatRoomDatasource): ChatDatasource

    @Binds
    abstract fun getRemoteChatHelper(remoteChatHelper: RemoteChatHelper): IRemoteChatHelper

    @Binds
    abstract fun getMediaDataSource(firebaseStorageDataSource: FirebaseStorageDataSource): MediaDataSource
}