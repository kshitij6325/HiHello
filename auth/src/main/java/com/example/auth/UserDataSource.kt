package com.example.auth
import com.example.pojo.Result

interface UserDataSource {
    suspend fun getUser(userId: String): Result<User>
    suspend fun createUser(user: User): Result<User>
    suspend fun updateUser(user: User): Result<User>
}