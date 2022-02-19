package com.example.auth.usecase

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.repo.UserRepository
import com.example.auth.repo.UserRepositoryImpl
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
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