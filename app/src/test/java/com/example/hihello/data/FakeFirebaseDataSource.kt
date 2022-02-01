package com.example.hihello.data

import android.app.Activity
import com.example.auth.datasource.FirebaseDataSource
import com.example.auth.datasource.PhoneVerification
import com.example.pojo.Result
import java.lang.Exception

class FakeFirebaseDataSource : FirebaseDataSource {

    var fail = false
    var verificationId = "verificationId"

    override suspend fun getAppSecret(): Result<String> {
        return if (!fail) Result.Success("Secret") else Result.Failure(Exception("Error fetching secret"))
    }

    override suspend fun getFirebaseToken(): Result<String> {
        return if (!fail) Result.Success("token") else Result.Failure(Exception("Error fetching token"))
    }

    override suspend fun verifyPhoneNumber(
        phoneNumber: String,
        activity: Activity
    ): Result<PhoneVerification> {
        return if (!fail) Result.Success(PhoneVerification.CodeSent(verificationId)) else Result.Failure(
            Exception("Error verifying phone number")
        )
    }

    override suspend fun verifyOtp(activity: Activity, otp: String, id: String): Result<Boolean> {
        return if (!fail) Result.Success(true) else Result.Failure(
            Exception("Error verifying OTP")
        )
    }
}