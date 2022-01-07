package com.example.hihello.home

import androidx.lifecycle.*
import com.example.auth.User
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.hihello.home.homeactivity.HomeUIState
import com.example.hihello.home.auth.login.SignInUiState
import com.example.hihello.home.auth.signup.SignUpUiState
import com.example.hihello.home.homefragment.HomeFragUiState
import com.example.pojo.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val logOutUseCase: LogoutUseCase

) : ViewModel() {
    private val homeScreenUiState = HomeUIState()
    private val signInScreenUiState = SignInUiState()
    private val signUpScreenUiState = SignUpUiState()
    private val homeFragUiState = HomeFragUiState()

    private val _signInScreenUiState = MutableStateFlow(signInScreenUiState)
    val signInScreenUiStateLiveData: StateFlow<SignInUiState> = _signInScreenUiState

    private val _signUpScreenUiState = MutableStateFlow(signUpScreenUiState)
    val signUpScreenUiStateLiveData: StateFlow<SignUpUiState> = _signUpScreenUiState

    private val _homeActivityUiState = MutableStateFlow(homeScreenUiState)
    val homeActivityUiStateLiveData: StateFlow<HomeUIState> = _homeActivityUiState

    private val _homeFragUiState = MutableStateFlow(homeFragUiState)
    val homeFragUiStateLiveData: StateFlow<HomeFragUiState> = _homeFragUiState

    fun isUserLoggedIn() = viewModelScope.launch {
        _homeActivityUiState.value = homeScreenUiState.setLoading(true)
        isUserLoggedInUseCase.apply {
            onSuccess = {
                _homeActivityUiState.value = homeScreenUiState.isLoggedIn(it)
                _homeActivityUiState.value = homeScreenUiState.setLoading(false)
            }
            onFailure = {
                _homeActivityUiState.value = homeScreenUiState.setLoggedInError(it.message)
                _homeActivityUiState.value = homeScreenUiState.setLoading(false)
            }
        }.invoke()
    }

    fun signUpUser(user: User) = viewModelScope.launch {
        _signUpScreenUiState.value = signUpScreenUiState.setLoading(true)
        signUpUseCase.apply {
            onSuccess = {
                _signUpScreenUiState.value = signUpScreenUiState.setSuccess(true)
                _signUpScreenUiState.value = signUpScreenUiState.setLoading(false)
            }
            onFailure = {
                _signUpScreenUiState.value = signUpScreenUiState.setError(it.message)
                _signUpScreenUiState.value = signUpScreenUiState.setLoading(false)
            }
        }.invoke(user)
    }

    fun signInUser(userId: String, password: String) = viewModelScope.launch {
        _signInScreenUiState.value = signInScreenUiState.setLoading(true)
        loginUseCase.apply {
            onSuccess = {
                _signInScreenUiState.value = signInScreenUiState.setSuccess(true)
                _signInScreenUiState.value = signInScreenUiState.setLoading(true)
            }
            onFailure = {
                _signInScreenUiState.value = signInScreenUiState.setError(it.message)
                _signInScreenUiState.value = signInScreenUiState.setLoading(true)
            }
        }.invoke(userId, password)
    }

    fun logOut() = viewModelScope.launch {
        _homeFragUiState.value = homeFragUiState.setLoading(true)
        logOutUseCase.apply {
            onSuccess = {
                _homeFragUiState.value = homeFragUiState.isLoggedOut(it)
                _homeFragUiState.value = homeFragUiState.setLoading(false)
            }
            onFailure = {
                _homeFragUiState.value = homeFragUiState.setLogOutError(it.message)
                _homeFragUiState.value = homeFragUiState.setLoading(false)
            }
        }.invoke()
    }

}