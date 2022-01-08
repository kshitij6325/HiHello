package com.example.auth.usecase

import com.example.auth.repo.UserRepositoryImpl
import com.example.pojo.BaseUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LogoutUseCase @Inject constructor(private val userRepositoryImpl: UserRepositoryImpl) :
    BaseUseCase<Boolean>() {

    suspend operator fun invoke() {
        userRepositoryImpl.deleteLocalUser().onSuccess {
            onSuccess?.invoke(it)
        }.onFailure {
            onFailure?.invoke(it)
        }
    }
}