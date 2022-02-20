package com.example.auth_feature.login

data class SignInUiState(
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val goToOtpScreen: Boolean = false,
)