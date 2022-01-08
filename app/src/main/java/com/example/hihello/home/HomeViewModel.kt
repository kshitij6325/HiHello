package com.example.hihello.home

import androidx.lifecycle.*
import com.example.auth.User
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.basefeature.update
import com.example.hihello.home.homeactivity.HomeUIState
import com.example.hihello.home.auth.login.SignInUiState
import com.example.hihello.home.auth.signup.SignUpUiState
import com.example.hihello.home.homefragment.HomeFragUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val logOutUseCase: LogoutUseCase
) : ViewModel() {

    private val _signInScreenUiState = MutableLiveData(SignInUiState())
    val signInScreenUiStateLiveData: LiveData<SignInUiState> = _signInScreenUiState

    private val _signUpScreenUiState = MutableLiveData(SignUpUiState())
    val signUpScreenUiStateLiveData: LiveData<SignUpUiState> = _signUpScreenUiState

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

    fun signUpUser(user: User) = viewModelScope.launch {
        _signUpScreenUiState.update {
            it?.copy(isLoading = true)
        }
        signUpUseCase.apply {
            onSuccess = {
                _signUpScreenUiState.update {
                    it?.copy(isSuccess = true, isLoading = false)
                }
            }
            onFailure = { ex ->
                _signUpScreenUiState.update {
                    it?.copy(error = ex.message, isLoading = false)
                }
            }
        }.invoke(user)
    }


    fun signInUser(userId: String, password: String) = viewModelScope.launch {
        _signInScreenUiState.update { it?.copy(isLoading = true) }
        loginUseCase.apply {
            onSuccess = {
                _signInScreenUiState.update { it?.copy(isLoading = false, isSuccess = true) }
            }
            onFailure = { error ->
                _signInScreenUiState.update { it?.copy(isLoading = false, error = error.message) }
            }
        }.invoke(userId, password)
    }

    fun logOut() = viewModelScope.launch {
        _homeFragUiState.update {
            it?.copy(isLogOutLoading = true)
        }
        logOutUseCase.apply {
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