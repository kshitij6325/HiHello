package com.example.auth.repo

import com.example.auth.User
import com.example.auth.WrongPasswordException
import com.example.auth.datasource.MeLocalDataSourceType
import com.example.auth.datasource.UserDataSource
import com.example.auth.datasource.UserFirebaseDataSourceType
import com.example.auth.datasource.UserRoomDataSourceType
import com.example.pojo.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @UserFirebaseDataSourceType
    private val userFirebaseDataSource: UserDataSource,
    @UserRoomDataSourceType
    private val userRoomDataSource: UserDataSource,
    @MeLocalDataSourceType
    private val meDataSource: UserDataSource
) {

    suspend fun signUpUser(user: User): Result<User> {
        return when (val res = userFirebaseDataSource.createUser(user)) {
            is Result.Failure -> res
            is Result.Success -> meDataSource.createUser(user)
        }
    }

    suspend fun getRemoteUser(user: User): Result<User> {
        return userFirebaseDataSource.getUser(userId = user.userName)
    }

    suspend fun getRemoteUserByPhone(user: User): Result<User> {
        return userFirebaseDataSource.getUserByMobile(mobile = user.mobileNumber ?: 0)
    }

    suspend fun loginUser(user: User, password: String): Result<User> {
        return when (val res = userFirebaseDataSource.getUser(user.userName)) {
            is Result.Failure -> res
            is Result.Success -> if (password == res.data.password) meDataSource.createUser(user) else Result.Failure(
                WrongPasswordException()
            )
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