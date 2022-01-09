package com.example.auth.data

import com.example.auth.NoSuchUserException
import com.example.auth.User
import com.example.auth.datasource.UserDataSource
import com.example.pojo.Result
import java.lang.Exception

class FakeUserDataSource(private val userList: MutableList<User>) : UserDataSource {

    var fail = false

    override suspend fun getUser(userId: String): Result<User> {
        return userList.find { it.userName == userId }?.let { Result.Success(it) }
            ?: Result.Failure(NoSuchUserException())
    }

    override suspend fun createUser(user: User): Result<User> {
        return if (!fail) {
            userList.add(user)
            Result.Success(user)
        } else {
            Result.Failure(Exception("Error creating user!"))
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return if (!fail) {
            val index = userList.indexOf(user)
            return if (index == -1) Result.Failure(NoSuchUserException()) else {
                userList.add(index, user)
                Result.Success(user)
            }
        } else {
            Result.Failure(Exception("Error creating user!"))
        }
    }

    override suspend fun getUserByMobile(mobile: Long): Result<User> {
        return userList.find { it.mobileNumber == mobile }?.let {
            Result.Success(it)
        } ?: Result.Failure(NoSuchUserException())
    }

    override suspend fun deleteUser(userId: String): Result<Boolean> {
        return if (!fail) {
            val index = userList.indexOfFirst { it.userName == userId }
            return if (index == -1) Result.Failure(NoSuchUserException()) else {
                userList.removeAt(index)
                Result.Success(true)
            }
        } else {
            Result.Failure(Exception("Error deleting user!"))
        }
    }
}