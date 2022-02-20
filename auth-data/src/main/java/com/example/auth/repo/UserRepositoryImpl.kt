package com.example.auth.repo

import com.example.auth.NoSuchUserException
import com.example.auth.UnknownUserException
import com.example.auth.User
import com.example.auth.UserAlreadyExitsException
import com.example.auth.datasource.*
import com.example.pojo.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @UserFirebaseDataSourceType
    private val userFirebaseDataSource: UserDataSource,
    @UserRoomDataSourceType
    private val userRoomDataSource: UserDataSource,
    @MeLocalDataSourceType
    private val meDataSource: UserDataSource,
) : UserRepository {

    override suspend fun createRemoteUser(user: User) = userFirebaseDataSource.createUser(user)

    override suspend fun createLoggedInUser(user: User) = meDataSource.createUser(user)

    override suspend fun createLocalUser(user: User): Result<Boolean> {
        return when (val userRes = userRoomDataSource.createUser(user)) {
            is Result.Failure -> Result.Failure(userRes.exception)
            is Result.Success -> Result.Success(true)
        }
    }

    override suspend fun getRemoteUser(userName: String): Result<User> {
        return userFirebaseDataSource.getUser(userId = userName)
    }

    override suspend fun getUserByMobile(mobile: Long): Result<User> {
        return userFirebaseDataSource.getUserByMobile(mobile)
    }

    override suspend fun getAllLocalUsers(): Result<List<User>> {
        return userRoomDataSource.getAllUsers()
    }

    override suspend fun deleteLocalUser() = meDataSource.deleteUser("")

    override suspend fun getLocalUser(userId: String): Result<User> {
        return userRoomDataSource.getUser(userId)
    }

    override suspend fun getLoggedInUser() = meDataSource.getUser("")

    override suspend fun isNewUser(user: User): Result<Boolean> {
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

    override suspend fun updateLoggedInUser(user: User): Result<User> {
        return when (val res = userFirebaseDataSource.updateUser(user)) {
            is Result.Failure -> res
            is Result.Success -> meDataSource.updateUser(user)
        }
    }

    override suspend fun updateLocalUser(user: User): Result<User> {
        return userRoomDataSource.updateUser(user)
    }

    override suspend fun createUserIfNotExists(userName: String): Result<User> {
        val userExistsRes = getLocalUser(userName)
        return when {
            userExistsRes is Result.Success -> Result.Success(userExistsRes.data)
            userExistsRes is Result.Failure && userExistsRes.exception is NoSuchUserException -> getRemoteUser(
                userName
            ).map { user ->
                createLocalUser(user).map {
                    Result.Success(user)
                }
            }
            userExistsRes is Result.Failure -> Result.Failure(userExistsRes.exception)
            else -> Result.Failure(UnknownUserException())
        }
    }
}