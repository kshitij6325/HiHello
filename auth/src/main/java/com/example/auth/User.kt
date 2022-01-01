package com.example.auth

data class User(
    val userId: String? = null,
    val fcmToken: String? = null,
    val userName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val mobileNumber: Long? = null
)
