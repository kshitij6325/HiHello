package com.example.auth.usecase

import com.example.auth.*
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.repo.UserRepository
import com.example.media_data.MediaRepository
import com.example.media_data.MediaSource
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SignUpUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepository,
    private val firebaseDataRepository: FirebaseDataRepository,
    private val mediaRepository: MediaRepository,
) :
    BaseUseCase<User>() {

    suspend operator fun invoke(user: User, avatarMediaSource: MediaSource.File? = null) {
        when {
            user.userName.isEmpty() -> onFailure?.invoke(EmptyUserNameException())
            !validateMobileNumber(user) -> onFailure?.invoke(InvalidPhoneNumberException())
            user.password.isNullOrEmpty() -> onFailure?.invoke(EmptyPasswordException())
            else -> {
                userRepositoryImpl.isNewUser(user).map {
                    firebaseDataRepository.getFirebaseToken().map { token ->
                        if (avatarMediaSource != null) {
                            mediaRepository.uploadMedia(avatarMediaSource, user.userName) {}.map {
                                userRepositoryImpl.createRemoteUser(
                                    user.copy(
                                        fcmToken = token,
                                        profileUrl = it.url
                                    )
                                ).map { user ->
                                    userRepositoryImpl.createLoggedInUser(user)
                                        .onSuccess { userLoggedIn ->
                                            onSuccess?.invoke(userLoggedIn)
                                        }
                                }

                            }
                        } else {
                            userRepositoryImpl.createRemoteUser(user.copy(fcmToken = token))
                                .map { user ->
                                    userRepositoryImpl.createLoggedInUser(user)
                                        .onSuccess { userLoggedIn ->
                                            onSuccess?.invoke(userLoggedIn)
                                        }
                                }
                        }

                    }
                }.catch {
                    onFailure?.invoke(it)
                }
            }

        }
    }


    private fun validateMobileNumber(user: User): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(user.mobileNumber.toString())
    }
}