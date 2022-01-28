package com.example.auth_feature.signup

import android.net.Uri

data class SignUpUiState(
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val imageUri: Uri? = null
)