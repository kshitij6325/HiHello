package com.example.chat_data.repo

import com.example.auth.User
import com.example.chat_data.Chat
import com.example.chat_data.datasource.ChatDatasource
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val remoteChatHelper: IRemoteChatHelper,
    private val chatRoomDatasource: ChatDatasource
) {

    suspend fun sendChat(user: User, appSecret: String, chat: Chat) =
        remoteChatHelper.sendMessageToDevice(user, appSecret, chat)

    suspend fun addChat(chat: Chat) = chatRoomDatasource.addChats(chat)

    suspend fun updateChatSuccess(chat: String, success: Boolean) =
        chatRoomDatasource.updateChatSuccessState(chat, success)

    suspend fun getAllUserChat(userId: String, limit: Int = 10) =
        chatRoomDatasource.getAllUserChat(userId, limit)

    fun getAllUserChatLiveData(userId: String) =
        chatRoomDatasource.getAllUserChatLiveData(userId)

    suspend fun getAllUnSendChats() = chatRoomDatasource.getAllUnSendChat()

    suspend fun getChat(id: Long) = chatRoomDatasource.getChat(id.toString())

    fun getAllUserChat() = chatRoomDatasource.getAllUserChatsMap()
}