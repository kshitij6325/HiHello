package com.example.chat_data.data.repo

import com.example.auth.data.User
import com.example.chat_data.data.Chat
import com.example.pojo.Result

interface IRemoteChatHelper {
    suspend fun sendMessageToDevice(
        user: User,
        appSecret: String,
        chat: Chat,
    ): Result<String>
}