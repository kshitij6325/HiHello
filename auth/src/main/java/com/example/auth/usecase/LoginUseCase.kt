package com.example.auth.usecase

import com.example.auth.EmptyPasswordException
import com.example.auth.EmptyUserNameException
import com.example.auth.User
import com.example.auth.WrongPasswordException
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LoginUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<User>() {

    suspend operator fun invoke(userName: String, password: String) {
        if (userName.isEmpty()) {
            onFailure?.invoke(EmptyUserNameException())
            return
        }
        if (password.isEmpty()) {
            onFailure?.invoke(EmptyPasswordException())
            return
        }
        userRepository.getRemoteUser(userName).map {
            (if (password == it.password) userRepository.crateLoggedInUser(it) else Result.Failure(
                WrongPasswordException()
            )).onSuccess { loggedInUser ->
                onSuccess?.invoke(loggedInUser)
            }
        }.catch {
            onFailure?.invoke(it)
        }
    }
}
