package com.example.hihello.home.homefragment

data class HomeFragUiState(
    val isLogOutLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorLogOut: String? = null
)