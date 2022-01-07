package com.example.hihello.home.homefragment

import com.example.hihello.home.homeactivity.HomeUIState

data class HomeFragUiState(
    val isLogOutLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorLogOut: String? = null
) {

    fun setLogOutError(error: String?): HomeFragUiState {
        return copy(errorLogOut = error)
    }

    fun isLoggedOut(isLoggedOut: Boolean): HomeFragUiState {
        return copy(isLoggedOut = isLoggedOut)
    }

    fun setLoading(isLoading: Boolean): HomeFragUiState {
        return copy(isLogOutLoading = isLoading)
    }
}