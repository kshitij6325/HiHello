package com.example.auth_feature

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.auth.data.User
import com.example.auth.data.datasource.State
import com.example.auth.domain.LoginUseCase
import com.example.auth.domain.SignUpUseCase
import com.example.auth_feature.login.SignInFragmentDirections
import com.example.auth_feature.login.SignInUiState
import com.example.auth_feature.signup.SignUpFragmentDirections
import com.example.auth_feature.signup.SignUpUiState
import com.example.basefeature.getFile
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {

    private val _signInScreenUiState = MutableStateFlow(SignInUiState())
    val signInScreenUiState = _signInScreenUiState.asStateFlow()

    private val _signUpScreenUiState = MutableStateFlow(SignUpUiState())
    val signUpScreenUiState = _signUpScreenUiState.asStateFlow()

    private val _otpUiState = MutableStateFlow(SignUpUiState())
    val otpScreenUiState = _otpUiState.asStateFlow()

    private val _toastError = MutableSharedFlow<String?>()
    val toastError = _toastError.asSharedFlow()

    private var cachedUserForOtp: User? = null
    private var isFromLogin = true

    fun setUserAvatar(uri: Uri?) {
        _signUpScreenUiState.update {
            it.copy(imageUri = uri)
        }
    }

    fun submitOtp(otp: String, activity: Activity) = viewModelScope.launch {
        _otpUiState.update {
            it.copy(isLoading = true)
        }
        val avatarMediaSrc =
            _signUpScreenUiState.value.imageUri?.getFile(activity.contentResolver)?.let {
                MediaSource.File(it, MediaType.IMAGE)
            }
        cachedUserForOtp?.let {
            if (isFromLogin) {
                loginUseCase.apply {
                    onSuccess = {
                        _otpUiState.update {
                            it.copy(isSuccess = true, isLoading = false)
                        }
                    }
                    onFailure = { ex ->
                        _otpUiState.update {
                            it.copy(error = ex.message, isLoading = false)
                        }
                        _toastError.emit(ex.message)

                    }
                }.invoke(user = it, activity, otp)
            } else {
                signUpUseCase.apply {
                    onSuccess = {
                        _otpUiState.update {
                            it.copy(isSuccess = true, isLoading = false)
                        }
                    }
                    onFailure = { ex ->
                        _otpUiState.update {
                            it.copy(error = ex.message, isLoading = false)
                        }
                        _toastError.emit(ex.message)

                    }

                }.invoke(
                    user = it,
                    activity = activity,
                    otp = otp,
                    avatarMediaSource = avatarMediaSrc
                )
            }
        }
    }

    fun signUpUser(user: User, activity: Activity) = viewModelScope.launch {
        val avatarMediaSrc =
            _signUpScreenUiState.value.imageUri?.getFile(activity.contentResolver)?.let {
                MediaSource.File(it, MediaType.IMAGE)
            }
        _signUpScreenUiState.update {
            it.copy(isLoading = true)
        }
        signUpUseCase.apply {
            onSuccess = {
                when (it) {
                    is State.Complete -> _signUpScreenUiState.update {
                        it.copy(isSuccess = true, isLoading = false)
                    }
                    is State.Otp -> {
                        cachedUserForOtp = it.user
                        isFromLogin = false
                        _signUpScreenUiState.update {
                            it.copy(goToOtp = true, isLoading = false)
                        }
                    }
                }

            }
            onFailure = { ex ->
                _signUpScreenUiState.update {
                    it.copy(error = ex.message, isLoading = false)
                }
                _toastError.emit(ex.message)
            }
        }.invoke(user = user, activity = activity, avatarMediaSource = avatarMediaSrc)
    }


    fun signInUser(phoneNumber: String, activity: Activity) = viewModelScope.launch {
        _signInScreenUiState.update {
            it.copy(isLoading = true)
        }
        loginUseCase.apply {
            onSuccess = {
                when (it) {
                    is State.Complete -> _signInScreenUiState.update {
                        it.copy(isSuccess = true, isLoading = false)
                    }
                    is State.Otp -> {
                        cachedUserForOtp = it.user
                        isFromLogin = true
                        _signInScreenUiState.update {
                            it.copy(goToOtpScreen = true, isLoading = false)
                        }
                    }
                }
            }
            onFailure = { error ->
                _signInScreenUiState.update { it.copy(isLoading = false, error = error.message) }
                _toastError.emit(error.message)
            }
        }.invoke(User(mobileNumber = phoneNumber.toLongOrNull()), activity)
    }

    fun navigateToOtp(navigator: (NavDirections) -> Unit) {
        navigator(
            if (isFromLogin) SignInFragmentDirections.actionSignInFragmentToOtpFragment()
            else
                SignUpFragmentDirections.actionSignUpFragmentToOtpFragment()
        )
        if (isFromLogin) {
            _signInScreenUiState.update {
                SignInUiState()
            }
        } else {
            _signUpScreenUiState.update {
                SignUpUiState()
            }
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