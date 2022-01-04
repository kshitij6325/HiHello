package com.example.hihello

import androidx.lifecycle.*
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.pojo.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val isUserLoggedInUseCase: IsUserLoggedInUseCase) :
    ViewModel() {

    private val _isUserLoggedInLiveData = MutableLiveData<UIState<Boolean>>()
    val isUserLoggedInLiveData: LiveData<UIState<Boolean>> = _isUserLoggedInLiveData

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

}