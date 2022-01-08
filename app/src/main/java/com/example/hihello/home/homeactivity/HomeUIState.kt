package com.example.hihello.home.homeactivity

data class HomeUIState(
    val isLoggedIn: Boolean = false,
    val isLoggedInError: String? = null,
    val isLoading: Boolean = false
)