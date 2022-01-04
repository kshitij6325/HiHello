package com.example.auth.usecase

import com.example.auth.InvalidPhoneNumberException
import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.UserAlreadyExitsException
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<User>() {

    suspend operator fun invoke(user: User) {
        when {
            !validateMobileNumber(user) -> onFailure?.invoke(InvalidPhoneNumberException())

            else -> {
                userRepository.isNewUser(user).map {
                    userRepository.getFirebaseToken().map {
                        userRepository.createRemoteUser(user.copy(fcmToken = it)).map { user ->
                            userRepository.crateLoggedInUser(user).onSuccess { userLoggedIn ->
                                onSuccess?.invoke(userLoggedIn)
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