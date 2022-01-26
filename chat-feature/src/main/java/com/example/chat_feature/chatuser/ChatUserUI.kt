package com.example.chat_feature.chatuser

import com.example.auth.User
import com.example.chat_data.Chat
import com.example.media_data.MediaSource
import java.io.File

data class ChatUserUI(
    val error: String? = null,
    val chatList: List<Chat> = listOf(),
    val currentUser: User? = null,
    val chatSentSuccess: Boolean = false,
    val mediaSource: MediaSource.File? = null
)