package com.example.auth.datasource

import com.example.pojo.Result

interface FirebaseDataSource {
    suspend fun getAppSecret(): Result<String>

    suspend fun getFirebaseToken(): Result<String>
}