package com.example.auth.data.datasource

import com.example.auth.data.User
import com.example.pojo.Result

interface UserDataSource {
    suspend fun getUser(userId: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>> {
        return Result.Success(emptyList())
    }

    suspend fun createUser(user: User): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun getUserByMobile(mobile: Long): Result<User>
    suspend fun deleteUser(userId: String): Result<Boolean>
}