package com.example.hihello.home

import androidx.lifecycle.*
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.basefeature.update
import com.example.hihello.home.homeactivity.HomeUIState
import com.example.hihello.home.homefragment.HomeFragUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserLoggedInUseCase: GetUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    // private val getAllUserChatUseCase: GetAllUserChatUseCase,
    // private val sendChatUseCase: SendChatUseCase
) : ViewModel() {

    private val _homeActivityUiState = MutableLiveData(HomeUIState())
    val homeActivityUiStateLiveData: LiveData<HomeUIState> = _homeActivityUiState

    private val _homeFragUiState = MutableLiveData(HomeFragUiState())
    val homeFragUiStateLiveData: LiveData<HomeFragUiState> = _homeFragUiState

    init {
        observeUserSentChat("kshitij")
    }

    fun isUserLoggedIn() = viewModelScope.launch {
        _homeActivityUiState.update {
            it?.copy(isLoading = true)
        }
        getUserLoggedInUseCase.apply {
            onSuccess = { user ->
                _homeActivityUiState.update {
                    it?.copy(isLoading = false, isLoggedIn = user != null)
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

    private fun observeUserSentChat(userId: String) {
        /*getAllUserChatUseCase.get(userId).observeForever { chatList ->
            _homeFragUiState.update {
                it?.copy(currentUserChat = chatList)
            }
        }*/
    }

    fun sendChat(message: String = "happy birthday", user_id: String = "kshitij") =
        viewModelScope.launch {
            /* sendChatUseCase.apply {
                 onSuccess = {
                     _homeFragUiState.update {
                         it?.copy(chatSuccess = "Sent!")
                     }
                 }
                 onFailure = { ex ->
                     _homeFragUiState.update {
                         it?.copy(chatError = ex.toString())
                     }
                 }
             }.invoke(message, user_id)*/
        }
}