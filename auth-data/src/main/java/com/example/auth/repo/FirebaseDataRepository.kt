package com.example.auth.repo

import com.example.auth.datasource.FirebaseDataSource
import com.example.pojo.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataRepository @Inject constructor(private val firebaseDataSource: FirebaseDataSource) {

    private var appSecret: Result<String>? = null

    suspend fun getAppSecret(cached: Boolean = true): Result<String> {
        return when {
            !cached || appSecret is Result.Failure || appSecret == null -> firebaseDataSource.getAppSecret()
                .also {
                    appSecret = it
                }
            else -> appSecret!!
        }
    }

    suspend fun getFirebaseToken(): Result<String> = firebaseDataSource.getFirebaseToken()
}