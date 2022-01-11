package com.example.auth.repo

import com.example.auth.datasource.FirebaseDataSourceImpl
import com.example.pojo.Result

object FirebaseDataRepository {

    private val firebaseDataSource by lazy { FirebaseDataSourceImpl() }

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
}