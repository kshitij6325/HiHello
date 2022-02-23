package com.example.chat_feature.chathome

import com.example.auth.data.User
import com.example.chat_data.data.Chat

data class ChatHomeUI(
    val error: String? = null,
    val loading: Boolean = false,
    val welcomeString: String? = null,
    val userChatList: List<Pair<User, Chat>> = listOf(),
    val userSyncing: Boolean = false,
    val userSyncSuccess: Boolean? = null,
    val userAvatar: String? = null
)