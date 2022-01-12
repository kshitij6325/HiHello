package com.example.hihello.home

import android.util.Log
import androidx.lifecycle.*
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.basefeature.update
import com.example.chat_data.usecase.SendChatUseCase
import com.example.hihello.home.homeactivity.HomeUIState
import com.example.hihello.home.homefragment.HomeFragUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _homeActivityUiState = MutableLiveData(HomeUIState())
    val homeActivityUiStateLiveData: LiveData<HomeUIState> = _homeActivityUiState

    private val _homeFragUiState = MutableLiveData(HomeFragUiState())
    val homeFragUiStateLiveData: LiveData<HomeFragUiState> = _homeFragUiState

    fun isUserLoggedIn() = viewModelScope.launch {
        _homeActivityUiState.update {
            it?.copy(isLoading = true)
        }
        isUserLoggedInUseCase.apply {
            onSuccess = { isLoggedIn ->
                _homeActivityUiState.update {
                    it?.copy(isLoading = false, isLoggedIn = isLoggedIn)
                }
            }
            onFailure = { ex ->
                _homeActivityUiState.update {
                    it?.copy(isLoading = false, isLoggedInError = ex.message)
                }
            }
        }.invoke()
    }

    fun logOut() = viewModelScope.launch {
        _homeFragUiState.update {
            it?.copy(isLogOutLoading = true)
        }
        logoutUseCase.apply {
            onSuccess = { loggedOut ->
                _homeFragUiState.update {
                    it?.copy(isLogOutLoading = false, isLoggedOut = loggedOut)
                }
            }
            onFailure = { ex ->
                _homeFragUiState.update {
                    it?.copy(isLogOutLoading = false, errorLogOut = ex.message)
                }
            }
        }.invoke()
    }
}