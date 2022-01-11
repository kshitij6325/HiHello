package com.example.auth.data

import com.example.auth.datasource.FirebaseDataSource
import com.example.pojo.Result
import java.lang.Exception

class FakeFirebaseDataSource : FirebaseDataSource {

    var fail = false

    override suspend fun getAppSecret(): Result<String> {
        return if (!fail) Result.Success("Secret") else Result.Failure(Exception("Error fetching secret"))
    }

    override suspend fun getFirebaseToken(): Result<String> {
        return if (!fail) Result.Success("token") else Result.Failure(Exception("Error fetching token"))
    }
}