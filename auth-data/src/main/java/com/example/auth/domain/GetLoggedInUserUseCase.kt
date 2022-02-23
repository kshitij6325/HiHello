package com.example.auth.domain

import com.example.auth.NoSuchUserException
import com.example.auth.data.User
import com.example.auth.data.repo.UserRepository
import com.example.pojo.BaseUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoggedInUserUseCase @Inject constructor(private val userRepositoryImpl: UserRepository) :
    BaseUseCase<User?>() {

    suspend operator fun invoke() {
        userRepositoryImpl.getLoggedInUser().onSuccess {
            onSuccess?.invoke(it)
        }.onFailure {
            if (it is NoSuchUserException) {
                onSuccess?.invoke(null)
            } else {
                onFailure?.invoke(it)
            }
        }
    }
}