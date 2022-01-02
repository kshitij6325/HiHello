package com.example.auth.repo

import com.example.auth.User
import com.example.auth.datasource.UserDataSource
import com.example.pojo.Result


class UserRepository(
    private val userFirebaseDataSource: UserDataSource,
    private val userRoomDataSource: UserDataSource,
    private val meDataSource: UserDataSource
) {

    suspend fun signUpUser(user: User): Result<User> {
        return when (val res = userFirebaseDataSource.createUser(user)) {
            is Result.Failure -> res
            is Result.Success -> meDataSource.createUser(user)
        }
    }

    suspend fun getLocalUser(userId: String): Result<User> {
        return userRoomDataSource.getUser(userId)
    }

    suspend fun getLoggedInUser() = meDataSource.getUser("")

    suspend fun updateLoggedInUser(user: User): Result<User> {
        return when (val res = userFirebaseDataSource.updateUser(user)) {
            is Result.Failure -> res
            is Result.Success -> meDataSource.updateUser(user)
        }
    }
}