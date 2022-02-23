package com.example.auth_feature.data

import com.example.auth.data.User
import com.example.auth.data.datasource.FirebaseDataSource
import com.example.auth.data.datasource.UserDataSource
import com.example.auth.data.repo.*

object FakeDataProvider {

    val myUser = User(
        userName = "kshitij6325",
        mobileNumber = 9953319602,
        profileUrl = "https://google.com.jpeg",
    )

    val user1 = User(
        userName = "harshit3344",
        mobileNumber = 9953319605,
        profileUrl = "https://google.com.jpeg",
    )

    val user2 = User(
        userName = "beena345",
        mobileNumber = 9953219602,
        profileUrl = "https://google.com.jpeg",
    )

    val user3 = User(
        userName = "surendra6677",
        mobileNumber = 9151319602,
        profileUrl = "https://google.com.jpeg",
    )

    private val remoteUserList =
        listOf(user1, user2, user3)
    private val localUserList = listOf<User>()

    private lateinit var userFirebaseDataSource: UserDataSource
    private lateinit var meDataSource: UserDataSource
    private lateinit var userRoomDataSource: UserDataSource
    private lateinit var firebaseDataSource: FirebaseDataSource

    fun initDataAndRepo(): UserRepository {
        userFirebaseDataSource = FakeUserDataSource(remoteUserList.toMutableList())
        meDataSource = FakeMeDataSource()
        userRoomDataSource = FakeUserDataSource(localUserList.toMutableList())
        firebaseDataSource = FakeFirebaseDataSource()
        return UserRepositoryImpl(
            userFirebaseDataSource = userFirebaseDataSource,
            meDataSource = meDataSource,
            userRoomDataSource = userRoomDataSource
        )
    }


}