package com.example.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey @ColumnInfo(name = "user_name") val userName: String? = null,
    @ColumnInfo(name = "fcm_token") val fcmToken: String? = null,
    @ColumnInfo(name = "first_name") val firstName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String? = null,
    @ColumnInfo(name = "mobile_number") val mobileNumber: Long? = null
)
