package com.example.chat_data.data

import com.example.auth.data.User
import com.example.chat_data.data.repo.IRemoteChatHelper
import com.example.pojo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.Exception

class FakeRemoteChatHelper : IRemoteChatHelper {
    var error = false
    override suspend fun sendMessageToDevice(
        user: User,
        appSecret: String,
        chat: Chat
    ): Result<String> = withContext(Dispatchers.IO) {
        delay(2000)
        return@withContext if (error) Result.Failure(Exception("Network error!")) else Result.Success(
            "Success"
        )
    }
}