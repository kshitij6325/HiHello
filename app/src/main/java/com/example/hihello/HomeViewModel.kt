package com.example.hihello

import androidx.lifecycle.*
import com.example.auth.User
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.pojo.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase

) : ViewModel() {

    private val _isUserLoggedInLiveData = MutableLiveData<UIState<Boolean>>()
    val isUserLoggedInLiveData: LiveData<UIState<Boolean>> = _isUserLoggedInLiveData

    private val _signInLiveData = MutableLiveData<UIState<User>>()
    val signInLiveData: LiveData<UIState<User>> = _signInLiveData

    private val _signUpLiveData = MutableLiveData<UIState<User>>()
    val signUpLiveData: LiveData<UIState<User>> = _signUpLiveData

    init {
        isUserLoggedIn()
    }


    private fun isUserLoggedIn() = viewModelScope.launch {
        _isUserLoggedInLiveData.postValue(UIState.Loading())
        isUserLoggedInUseCase.apply {
            onSuccess = {
                _isUserLoggedInLiveData.postValue(UIState.Success(it))
            }
            onFailure = {
                _isUserLoggedInLiveData.postValue(UIState.Failure(it.message.toString()))
            }
        }.invoke()
    }

    fun signUpUser(user: User) = viewModelScope.launch {
        _signUpLiveData.postValue(UIState.Loading())
        signUpUseCase.apply {
            onSuccess = {
                _signUpLiveData.postValue(UIState.Success(it))
            }
            onFailure = {
                _signUpLiveData.postValue(UIState.Failure(it.message.toString()))
            }
        }.invoke(user)
    }

    fun signInUser(userId: String, password: String) = viewModelScope.launch {
        _signInLiveData.postValue(UIState.Loading())
        loginUseCase.apply {
            onSuccess = {
                _signInLiveData.postValue(UIState.Success(it))
            }
            onFailure = {
                _signInLiveData.postValue(UIState.Failure(it.message.toString()))
            }
        }.invoke(userId, password)
    }

}