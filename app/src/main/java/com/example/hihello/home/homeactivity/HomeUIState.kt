package com.example.hihello.home.homeactivity

data class HomeUIState(
    val isLoggedIn: Boolean = false,
    val isLoggedInError: String? = null,
    val isLoading: Boolean = false
) {

    fun setLoggedInError(error: String?): HomeUIState {
        return copy(isLoggedInError = error)
    }

    fun isLoggedIn(isLoggedIn: Boolean): HomeUIState {
        return copy(isLoggedIn = isLoggedIn)
    }

    fun setLoading(isLoading: Boolean): HomeUIState {
        return copy(isLoading = isLoading)
    }
}