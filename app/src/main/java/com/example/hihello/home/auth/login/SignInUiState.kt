package com.example.hihello.home.auth.login

data class SignInUiState(
    val error: String? = null, val isSuccess: Boolean = false, val isLoading: Boolean = false
)