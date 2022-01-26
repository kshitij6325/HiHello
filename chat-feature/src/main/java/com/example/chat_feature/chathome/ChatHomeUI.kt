package com.example.chat_feature.chathome

import com.example.auth.User
import com.example.chat_data.Chat
import java.io.File

data class ChatHomeUI(
    val error: String? = null,
    val loading: Boolean = false,
    val welcomeString: String? = null,
    val userChatList: List<Pair<User, Chat>> = listOf(),
    val userSyncing: Boolean = false,
    val userSyncSuccess: Boolean? = null,
)