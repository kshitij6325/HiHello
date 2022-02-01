package com.example.auth.usecase

import android.app.Activity
import com.example.auth.*
import com.example.auth.datasource.PhoneVerification
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

sealed class State(val user: User) {
    class Otp(val mUser: User) : State(mUser)
    class Complete(val mUser: User) : State(mUser)
}

@ViewModelScoped
class SignUpUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepository,
    private val firebaseDataRepository: FirebaseDataRepository,
    private val mediaRepository: MediaRepository,
) : BaseUseCase<State>() {

    private var phoneAuthId: String? = null

    suspend operator fun invoke(
        user: User,
        activity: Activity,
        otp: String? = null,
        avatarMediaSource: MediaSource.File? = null,
    ) {
        if (otp != null) {
            phoneAuthId?.let {
                firebaseDataRepository.verifyOtp(activity, otp, it).onSuccess {
                    createUser(user, avatarMediaSource)
                }.onFailure { onFailure?.invoke(it) }
            }
        } else {
            when {
                user.userName.isEmpty() -> onFailure?.invoke(EmptyUserNameException())
                !validateMobileNumber(user) -> onFailure?.invoke(InvalidPhoneNumberException())
                user.password.isNullOrEmpty() -> onFailure?.invoke(EmptyPasswordException())
                else -> {
                    userRepositoryImpl.isNewUser(user).map {
                        firebaseDataRepository.verifyPhoneNumber(
                            "+91${user.mobileNumber.toString()}",
                            activity
                        ).onSuccess {
                            when (it) {
                                is PhoneVerification.CodeSent -> {
                                    this.phoneAuthId = it.id
                                    onSuccess?.invoke(State.Otp(user))
                                }
                                is PhoneVerification.Success -> createUser(user, avatarMediaSource)
                            }
                        }
                    }.catch {
                        onFailure?.invoke(it)
                    }
                }

            }
        }
    }

    private suspend fun createUser(user: User, avatarMediaSource: MediaSource.File? = null) =
        firebaseDataRepository.getFirebaseToken().map { token ->
            if (avatarMediaSource != null) {
                mediaRepository.uploadMedia(avatarMediaSource, user.userName) {}
                    .map {
                        userRepositoryImpl.createRemoteUser(
                            user.copy(
                                fcmToken = token,
                                profileUrl = it.url
                            )
                        ).map { user ->
                            userRepositoryImpl.createLoggedInUser(user)
                                .onSuccess { userLoggedIn ->
                                    onSuccess?.invoke(State.Complete(userLoggedIn))
                                }
                        }

                    }
            } else {
                userRepositoryImpl.createRemoteUser(user.copy(fcmToken = token))
                    .map { user ->
                        userRepositoryImpl.createLoggedInUser(user)
                            .onSuccess { userLoggedIn ->
                                onSuccess?.invoke(State.Complete(userLoggedIn))
                            }
                    }
            }
        }.catch { onFailure?.invoke(it) }

    private fun validateMobileNumber(user: User): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(user.mobileNumber.toString())
    }
}