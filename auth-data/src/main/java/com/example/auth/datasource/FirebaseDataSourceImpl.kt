package com.example.auth.datasource

import com.example.pojo.Result
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseDataSourceImpl @Inject constructor() : FirebaseDataSource {

    override suspend fun getAppSecret(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference("app_secret").get()
                .addOnSuccessListener {
                    continuation.resume(Result.Success(it.getValue(String::class.java) as String))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }
    }

    override suspend fun getFirebaseToken(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener {
                    continuation.resume(Result.Success(it))
                }
                .addOnFailureListener {
                    continuation.resume(Result.Failure(it))
                }
        }
    }
}