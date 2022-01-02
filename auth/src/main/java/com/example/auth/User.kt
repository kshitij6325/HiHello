package com.example.auth

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
    @Ignore var password: String? = null
)


class NoSuchUserException : Exception("No such user found")
class WrongPasswordException : Exception("Wrong password")
class UserAlreadyExitsException : Exception("User already exits")
class InvalidPhoneNumberException : Exception("Invalid mobile number")
