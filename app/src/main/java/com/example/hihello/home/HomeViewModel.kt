package com.example.hihello.home

import androidx.lifecycle.*
import com.example.auth.usecase.GetLoggedInUserUseCase
import com.example.hihello.home.homeactivity.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
) : ViewModel() {

    private val _homeActivityUiState = MutableStateFlow(HomeUIState())
    val homeActivityUiStateLiveData = _homeActivityUiState.asStateFlow()

    fun isUserLoggedIn() = viewModelScope.launch {
        _homeActivityUiState.update {
            it.copy(isLoading = true)
        }
        getLoggedInUserUseCase.apply {
            onSuccess = { user ->
                _homeActivityUiState.update {
                    it.copy(isLoading = false, isLoggedIn = user != null)
                }
            }
            onFailure = { ex ->
                _homeActivityUiState.update {
                    it.copy(isLoading = false, isLoggedInError = ex.message)
                }
            }
        }.invoke()
    }

}