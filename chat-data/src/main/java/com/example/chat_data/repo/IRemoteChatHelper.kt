package com.example.chat_data.repo

import com.example.auth.User
import com.example.chat_data.Chat
import com.example.pojo.Result

interface IRemoteChatHelper {
    suspend fun sendMessageToDevice(
        user: User,
        appSecret: String,
        chat: Chat,
    ): Result<String>
}