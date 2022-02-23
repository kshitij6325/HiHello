package com.example.auth.data

import com.example.auth.NoSuchUserException
import com.example.auth.data.datasource.UserDataSource
import com.example.pojo.Result
import java.lang.Exception

class FakeMeDataSource : UserDataSource {

    var user: User? = null
    var fail = false

    override suspend fun getUser(userId: String): Result<User> {
        if (fail) return Result.Failure(NoSuchUserException())
        return user?.let { Result.Success(it) } ?: Result.Failure(NoSuchUserException())
    }

    override suspend fun createUser(user: User): Result<User> {
        return if (!fail) {
            this.user = user
            Result.Success(user)
        } else {
            Result.Failure(Exception("Error creating user"))
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        if (fail) return Result.Failure(Exception("Error updating user"))
        return createUser(user)
    }

    override suspend fun getUserByMobile(mobile: Long): Result<User> {
        return getUser("")
    }

    override suspend fun deleteUser(userId: String): Result<Boolean> {
        if (fail) return Result.Failure(Exception("Error deleting user"))
        this.user = null
        return Result.Success(true)
    }
}