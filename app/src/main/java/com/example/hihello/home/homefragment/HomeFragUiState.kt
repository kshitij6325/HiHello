package com.example.hihello.home.homefragment

import com.example.chat_data.Chat

data class HomeFragUiState(
    val isLogOutLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorLogOut: String? = null,
    val chatError: String? = null,
    val chatSuccess: String? = null,
    val currentUserChat: List<Chat> = listOf()
)