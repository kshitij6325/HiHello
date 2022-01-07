package com.example.hihello.home.auth.login

data class SignInUiState(
    val error: String? = null, val isSuccess: Boolean = true, val isLoading: Boolean = false
) {

    fun setError(error: String?): SignInUiState {
        return copy(error = error)
    }

    fun setSuccess(isSuccess: Boolean): SignInUiState {
        return copy(isSuccess = isSuccess)
    }

    fun setLoading(isLoading: Boolean): SignInUiState {
        return copy(isLoading = isLoading)
    }
}