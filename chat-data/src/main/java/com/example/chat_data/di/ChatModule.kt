package com.example.chat_data.di

import com.example.chat_data.datasource.ChatDatasource
import com.example.chat_data.datasource.ChatRoomDatasource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ChatModule {

    @Binds
    fun getChatDataSource(chatRoomDatasource: ChatRoomDatasource): ChatDatasource
}