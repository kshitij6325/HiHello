package com.example.auth.usecase

import com.example.auth.NoSuchUserException
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class IsUserLoggedInUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<Boolean>() {

    suspend operator fun invoke() {
        userRepository.getLoggedInUser().onSuccess {
            onSuccess?.invoke(true)
        }.onFailure {
            if (it is NoSuchUserException) {
                onSuccess?.invoke(false)
            } else {
                onFailure?.invoke(it)
            }
        }
    }
}