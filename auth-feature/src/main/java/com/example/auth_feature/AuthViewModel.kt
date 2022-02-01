package com.example.auth_feature

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.IdRes
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.room.PrimaryKey
import com.example.auth.User
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.auth.usecase.State
import com.example.auth_feature.login.SignInFragmentDirections
import com.example.auth_feature.login.SignInUiState
import com.example.auth_feature.signup.SignUpFragmentDirections
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
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {

    private val _signInScreenUiState = MutableLiveData(SignInUiState())
    val signInScreenUiStateLiveData: LiveData<SignInUiState> = _signInScreenUiState

    private val _signUpScreenUiState = MutableLiveData(SignUpUiState())
    val signUpScreenUiStateLiveData: LiveData<SignUpUiState> = _signUpScreenUiState

    private val _otpUiState = MutableLiveData(SignUpUiState())
    val otpScreenUiStateLiveData: LiveData<SignUpUiState> = _otpUiState

    private var cachedUserForOtp: User? = null

    fun setUserAvatar(uri: Uri?) {
        _signUpScreenUiState.update {
            it?.copy(imageUri = uri)
        }
    }

    fun submitOtp(otp: String, activity: Activity) = viewModelScope.launch {
        _otpUiState.update {
            it?.copy(isLoading = true)
        }
        val avatarMediaSrc =
            _signUpScreenUiState.value?.imageUri?.getFile(activity.contentResolver)?.let {
                MediaSource.File(it, MediaType.IMAGE)
            }
        cachedUserForOtp?.let {
            signUpUseCase.apply {
                onSuccess = {
                    _otpUiState.update {
                        it?.copy(isSuccess = true, isLoading = false)
                    }
                }
                onFailure = { ex ->
                    _otpUiState.update {
                        it?.copy(error = ex.message, isLoading = false)
                    }

                }

            }.invoke(user = it, activity = activity, otp = otp, avatarMediaSource = avatarMediaSrc)
        }
    }

    fun signUpUser(user: User, activity: Activity) = viewModelScope.launch {
        val avatarMediaSrc =
            _signUpScreenUiState.value?.imageUri?.getFile(activity.contentResolver)?.let {
                MediaSource.File(it, MediaType.IMAGE)
            }
        _signUpScreenUiState.update {
            it?.copy(isLoading = true)
        }
        signUpUseCase.apply {
            onSuccess = {
                when (it) {
                    is State.Complete -> _signUpScreenUiState.update {
                        it?.copy(isSuccess = true, isLoading = false)
                    }
                    is State.Otp -> {
                        cachedUserForOtp = it.user
                        _signUpScreenUiState.update {
                            it?.copy(goToOtp = true, isLoading = false)
                        }
                    }
                }

            }
            onFailure = { ex ->
                _signUpScreenUiState.update {
                    it?.copy(error = ex.message, isLoading = false)
                }
            }
        }.invoke(user = user, activity = activity, avatarMediaSource = avatarMediaSrc)
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

    fun navigateToOtp(navigator: (NavDirections) -> Unit) {
        navigator(SignUpFragmentDirections.actionSignUpFragmentToOtpFragment())
        _signUpScreenUiState.update {
            SignUpUiState()
        }
    }

    fun navigateToSignIn(navigator: (NavDirections) -> Unit) {
        navigator(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
    }

    fun navigateToSignUp(navigator: (NavDirections) -> Unit) {
        navigator(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
    }

    fun navigateToChat(activity: Activity, navigator: (String, Int?) -> Unit) {
        navigator(
            activity.resources.getString(R.string.chat_home_frag_deeplink_string),
            R.id.auth_nav
        )
    }

}