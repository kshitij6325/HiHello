package com.example.hihello.home.auth.signup

data class SignUpUiState(
    val error: String? = null, val isSuccess: Boolean = false, val isLoading: Boolean = false
) {

    fun setError(error: String?): SignUpUiState {
        return copy(error = error)
    }

    fun setSuccess(isSuccess: Boolean): SignUpUiState {
        return copy(isSuccess = isSuccess)
    }

    fun setLoading(isLoading: Boolean): SignUpUiState {
        return copy(isLoading = isLoading)
    }
}