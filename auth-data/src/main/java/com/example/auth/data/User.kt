package com.example.auth.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.lang.Exception

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey @ColumnInfo(name = "user_name") var userName: String = "",
    @ColumnInfo(name = "fcm_token") var fcmToken: String? = null,
    @ColumnInfo(name = "mobile_number") var mobileNumber: Long? = null,
    @ColumnInfo(name = "profile_url") var profileUrl: String? = null,
)