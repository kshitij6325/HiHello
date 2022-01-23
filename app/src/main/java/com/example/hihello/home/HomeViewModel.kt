package com.example.hihello.home

import androidx.lifecycle.*
import com.example.auth.usecase.GetUserLoggedInUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.basefeature.update
import com.example.hihello.home.homeactivity.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserLoggedInUseCase: GetUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _homeActivityUiState = MutableLiveData(HomeUIState())
    val homeActivityUiStateLiveData: LiveData<HomeUIState> = _homeActivityUiState

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

}