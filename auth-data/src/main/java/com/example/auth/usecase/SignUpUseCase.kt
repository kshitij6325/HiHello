package com.example.auth.usecase

import com.example.auth.*
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SignUpUseCase @Inject constructor(private val userRepositoryImpl: UserRepository) :
    BaseUseCase<User>() {

    suspend operator fun invoke(user: User) {
        when {
            user.userName.isEmpty() -> onFailure?.invoke(EmptyUserNameException())
            !validateMobileNumber(user) -> onFailure?.invoke(InvalidPhoneNumberException())
            user.password.isNullOrEmpty() -> onFailure?.invoke(EmptyPasswordException())
            else -> {
                userRepositoryImpl.isNewUser(user).map {
                    userRepositoryImpl.getFirebaseToken().map {
                        userRepositoryImpl.createRemoteUser(user.copy(fcmToken = it)).map { user ->
                            userRepositoryImpl.crateLoggedInUser(user).onSuccess { userLoggedIn ->
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