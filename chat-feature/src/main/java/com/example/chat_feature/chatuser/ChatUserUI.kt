package com.example.chat_feature.chatuser

import com.example.auth.User
import com.example.chat_data.Chat

data class ChatUserUI(
    val error: String? = null,
    val chatList: List<Chat> = listOf(),
    val currentUser: User? = null,
    val chatSentSuccess: Boolean = false
)