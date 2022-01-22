package com.example.chat_feature.chathome

import com.example.auth.User
import com.example.chat_data.Chat

data class ChatHomeUI(
    val error: String? = null,
    val loading: Boolean = false,
    val welcomeString: String? = null,
    val userChatList: List<Pair<User, Chat>> = listOf()
)