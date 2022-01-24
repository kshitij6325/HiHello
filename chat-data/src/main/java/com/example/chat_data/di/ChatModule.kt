package com.example.chat_data.di

import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.chat_data.datasource.ChatDatasource
import com.example.chat_data.datasource.ChatRoomDatasource
import com.example.chat_data.repo.IRemoteChatHelper
import com.example.chat_data.repo.RemoteChatHelper
import com.example.pojo.Result
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {

    @Binds
    fun getChatDataSource(chatRoomDatasource: ChatRoomDatasource): ChatDatasource

    @Binds
    fun getRemoteChatHelper(remoteChatHelper: RemoteChatHelper): IRemoteChatHelper
}