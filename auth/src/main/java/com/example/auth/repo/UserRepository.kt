package com.example.auth.repo

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.UserAlreadyExitsException
import com.example.auth.WrongPasswordException
import com.example.auth.datasource.*
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class UserRepository @Inject constructor(
    @UserFirebaseDataSourceType
    private val userFirebaseDataSource: UserDataSource,
    @UserRoomDataSourceType
    private val userRoomDataSource: UserDataSource,
    @MeLocalDataSourceType
    private val meDataSource: UserDataSource,
    private val firebaseDataSource: FirebaseDataSource
) {

    suspend fun createRemoteUser(user: User) = userFirebaseDataSource.createUser(user)

    suspend fun crateLoggedInUser(user: User) = meDataSource.createUser(user)

    suspend fun getRemoteUser(userName: String): Result<User> {
        return userFirebaseDataSource.getUser(userId = userName)
    }

    suspend fun getAppSecret() = firebaseDataSource.getAppSecret()

    suspend fun getFirebaseToken() = firebaseDataSource.getFirebaseToken()

    suspend fun deleteLocalUser() = meDataSource.deleteUser("")

    suspend fun getLocalUser(userId: String): Result<User> {
        return userRoomDataSource.getUser(userId)
    }

    suspend fun getLoggedInUser() = meDataSource.getUser("")

    suspend fun isNewUser(user: User): Result<Boolean> {
        val res = userFirebaseDataSource.getUser(user.userName)
        return when {
            res is Result.Success -> Result.Failure(UserAlreadyExitsException())
            res is Result.Failure && res.exception !is NoSuchUserException -> Result.Failure(res.exception)
            else -> {
                val resPhone = userFirebaseDataSource.getUserByMobile(user.mobileNumber ?: 0)
                return when {
                    resPhone is Result.Success -> Result.Failure(UserAlreadyExitsException())
                    resPhone is Result.Failure && resPhone.exception !is NoSuchUserException -> Result.Failure(
                        resPhone.exception
                    )
                    else -> Result.Success(true)
                }
            }
        }
    }

    suspend fun updateLoggedInUser(user: User): Result<User> {
        return when (val res = userFirebaseDataSource.updateUser(user)) {
            is Result.Failure -> res
            is Result.Success -> meDataSource.updateUser(user)
        }
    }
}