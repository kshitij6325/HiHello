package com.example.auth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import com.example.pojo.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class UserRemoteDataSource : UserDataSource {

    private val firebaseInstanceRef by lazy {
        FirebaseDatabase.getInstance().getReference("user")
    }

    override suspend fun getUser(userId: String): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> {
                firebaseInstanceRef.child(userId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            (snapshot.getValue(User::class.java))?.run {
                                it.resume(Result.Success(this)) {}
                            }
                                ?: it.resume(Result.Failure(Exception("User is null"))) {}

                        }

                        override fun onCancelled(error: DatabaseError) {
                            it.resume(Result.Failure(error.toException())) {}
                        }

                    })
            }
        }

    override suspend fun createUser(user: User): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine<Result<User>> { continuation ->
                firebaseInstanceRef.child(user.userName ?: "").setValue(user).addOnSuccessListener {
                    continuation.resume(Result.Success(user)) {}
                }.addOnFailureListener {
                    continuation.resume(Result.Failure(it)) {}
                }
            }
        }

    override suspend fun updateUser(user: User): Result<User> =
        withContext(Dispatchers.IO) {
            return@withContext createUser(user)
        }
}