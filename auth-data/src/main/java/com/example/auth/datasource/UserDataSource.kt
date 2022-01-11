package com.example.auth.datasource

import com.example.auth.User
import com.example.pojo.Result

interface UserDataSource {
    suspend fun getUser(userId: String): Result<User>
    suspend fun createUser(user: User): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun getUserByMobile(mobile: Long): Result<User>
    suspend fun deleteUser(userId: String): Result<Boolean>
}