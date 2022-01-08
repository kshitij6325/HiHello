package com.example.auth.repo

import com.example.auth.datasource.IFirebaseDataSource
import com.example.pojo.Result
import java.lang.Exception

class FakeFirebaseDataSource : IFirebaseDataSource {

    var fail = false

    override suspend fun getAppSecret(): Result<String> {
        return if (!fail) Result.Success("Secret") else Result.Failure(Exception("Error fetching secret"))
    }

    override suspend fun getFirebaseToken(): Result<String> {
        return if (!fail) Result.Success("token") else Result.Failure(Exception("Error fetching token"))
    }
}