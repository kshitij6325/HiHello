package com.example.chat_data.repo

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
import javax.inject.Singleton

const val CHAT_DATA = "chat_data"

@Singleton
class RemoteChatHelper @Inject constructor() : IRemoteChatHelper {
    override suspend fun sendMessageToDevice(
        user: User,
        appSecret: String,
        chat: Chat,
    ): Result<String> = withContext(Dispatchers.IO) {
        val requestPayload = JSONObject()

        //setting chat data
        val data = JSONObject()
        data.put(CHAT_DATA, Json.encodeToString(chat))
        requestPayload.put("data", data)

        //setting registration id
        requestPayload.put("registration_ids", JSONArray(listOf(user.fcmToken).toTypedArray()))

        //adding priority data for message delivery
        val priorityData = JSONObject()
        priorityData.put("priority", "high")
        requestPayload.put("android", priorityData)

        val res = OkHttpClient().run {
            newCall(
                Request.Builder().apply {
                    url("https://fcm.googleapis.com/fcm/send")
                    post(
                        RequestBody.create(
                            MediaType.get("application/json; charset=utf-8"),
                            requestPayload.toString()
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