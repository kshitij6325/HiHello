package com.example.chat_data.repo

import android.content.ContentProviderOperation.newCall
import com.example.auth.User
import com.example.chat_data.Chat
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import javax.inject.Inject

@ViewModelScoped
class RemoteChatHelper @Inject constructor() {

    suspend fun sendMessageToDevice(
        user: User,
        appSecret: String,
        chat: Chat,
    ): Result<String> = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val notification = JSONObject()
        notification.put("body", "${chat.message}")
        notification.put("title", "Message by ${user.userName}")
        val data = JSONObject()
        data.put("extra_chat", Json.encodeToString(chat))
        root.put("notification", notification)
        root.put("data", data)
        root.put("registration_ids", JSONArray(listOf(user.fcmToken).toTypedArray()))

        val res = OkHttpClient().run {
            newCall(
                Request.Builder().apply {
                    url("https://fcm.googleapis.com/fcm/send")
                    post(
                        RequestBody.create(
                            MediaType.get("application/json; charset=utf-8"),
                            root.toString()
                        )
                    )
                    addHeader("Authorization", "key=$appSecret")
                }.build()
            )
        }.execute()
        return@withContext if (res.isSuccessful) {
            Result.Success(res.body().toString())
        } else {
            Result.Failure(Exception(res.body().toString()))
        }
    }
}