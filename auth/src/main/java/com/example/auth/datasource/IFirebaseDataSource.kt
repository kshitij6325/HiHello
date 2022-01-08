package com.example.auth.datasource

import com.example.pojo.Result

interface IFirebaseDataSource {
    suspend fun getAppSecret(): Result<String>

    suspend fun getFirebaseToken(): Result<String>
}