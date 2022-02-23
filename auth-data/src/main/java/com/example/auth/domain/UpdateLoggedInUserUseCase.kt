package com.example.auth.domain

import com.example.auth.data.User
import com.example.auth.data.repo.UserRepository
import com.example.pojo.BaseUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateLoggedInUserUseCase @Inject constructor(private val userRepository: UserRepository) :
    BaseUseCase<Boolean>() {


    suspend fun invoke(user: User) {
        userRepository.updateLoggedInUser(user).onSuccess {
            onSuccess?.invoke(true)
        }.onFailure {
            onFailure?.invoke(it)
        }
    }
}