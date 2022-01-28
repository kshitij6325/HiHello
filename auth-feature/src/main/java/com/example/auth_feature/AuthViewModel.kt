package com.example.auth_feature

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.*
import com.example.auth.User
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.auth_feature.login.SignInUiState
import com.example.auth_feature.signup.SignUpUiState
import com.example.basefeature.getFile
import com.example.basefeature.update
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _signInScreenUiState = MutableLiveData(SignInUiState())
    val signInScreenUiStateLiveData: LiveData<SignInUiState> = _signInScreenUiState

    private val _signUpScreenUiState = MutableLiveData(SignUpUiState())
    val signUpScreenUiStateLiveData: LiveData<SignUpUiState> = _signUpScreenUiState

    fun setUserAvatar(uri: Uri?) {
        _signUpScreenUiState.update {
            it?.copy(imageUri = uri)
        }
    }

    fun signUpUser(user: User, resolver: ContentResolver) = viewModelScope.launch {
        val avatarMediaSrc = _signUpScreenUiState.value?.imageUri?.getFile(resolver)?.let {
            MediaSource.File(it, MediaType.IMAGE)
        }
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
        }.invoke(user, avatarMediaSrc)
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

}