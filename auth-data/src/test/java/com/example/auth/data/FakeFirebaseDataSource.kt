package com.example.auth.data

import com.example.auth.datasource.FirebaseDataSource
import com.example.pojo.Result
import java.lang.Exception

class FakeFirebaseDataSource : FirebaseDataSource {

    var fail = false

    var firebaseToken = "token"
    var secret = "Secret"

    override suspend fun getAppSecret(): Result<String> {
        return if (!fail) Result.Success(secret) else Result.Failure(Exception("Error fetching secret"))
    }

    override suspend fun getFirebaseToken(): Result<String> {
        return if (!fail) Result.Success(firebaseToken) else Result.Failure(Exception("Error fetching token"))
    }
}