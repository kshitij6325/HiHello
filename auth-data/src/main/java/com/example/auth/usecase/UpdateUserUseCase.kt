package com.example.auth.usecase

import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.pojo.BaseUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<Boolean>() {


    suspend fun invoke(user: User) {
        userRepository.updateLoggedInUser(user).onSuccess {
            onSuccess?.invoke(true)
        }.onFailure {
            onFailure?.invoke(it)
        }
    }
}