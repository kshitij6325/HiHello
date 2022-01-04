package com.example.auth.usecase

import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LogoutUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<Boolean>() {

    suspend operator fun invoke() {
        userRepository.deleteLocalUser().onSuccess {
            onSuccess?.invoke(it)
        }.onFailure {
            onFailure?.invoke(it)
        }
    }
}