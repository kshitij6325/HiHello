package com.example.chat_data.di

import com.example.chat_data.datasource.ChatDatasource
import com.example.chat_data.datasource.ChatRoomDatasource
import com.example.chat_data.repo.IRemoteChatHelper
import com.example.chat_data.repo.RemoteChatHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {

    @Binds
    fun getChatDataSource(chatRoomDatasource: ChatRoomDatasource): ChatDatasource

    @Binds
    fun getRemoteChatHelper(remoteChatHelper: RemoteChatHelper): IRemoteChatHelper
}