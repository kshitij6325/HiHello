package com.example.hihello.repo

import com.example.auth.NoSuchUserException
import com.example.auth.UserAlreadyExitsException
import com.example.auth.repo.UserRepository
import com.example.hihello.data.FakeDataProvider
import com.example.pojo.Result
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserRepositoryImplTest {

    private lateinit var repo: UserRepository

    @Before
    @After
    fun initRepo() {
        repo = FakeDataProvider.initDataAndRepo()
    }

    @Test
    fun givenRepo_fetchRemoteUser() = runBlocking {
        val resCreate = repo.getRemoteUser(FakeDataProvider.user1.userName)
        assert(resCreate is Result.Success && resCreate.data.userName == FakeDataProvider.user1.userName)
    }

    @Test
    fun givenRepo_fetchRemoteUserError() = runBlocking {
        val resCreate = repo.getRemoteUser("userId12345")
        assert(resCreate is Result.Failure && resCreate.exception is NoSuchUserException)
    }

    @Test
    fun givenRepo_testLoggedInUser() = runBlocking {
        repo.crateLoggedInUser(FakeDataProvider.myUser)
        val loggedInUser = repo.getLoggedInUser()
        assert(loggedInUser is Result.Success && loggedInUser.data.userName == FakeDataProvider.myUser.userName)
    }

    @Test
    fun givenRepo_testLoggedInUserError() = runBlocking {
        val loggedInUser = repo.getLoggedInUser()
        assert(loggedInUser is Result.Failure && loggedInUser.exception is NoSuchUserException)
    }

    @Test
    fun givenRepo_testUserExists() = runBlocking {
        val user = repo.isNewUser(FakeDataProvider.myUser)
        assert(user is Result.Success)
    }

    @Test
    fun givenRepo_testLogout() = runBlocking {
        val resCreate = repo.crateLoggedInUser(FakeDataProvider.myUser)
        val resLogout = repo.deleteLocalUser()
        assert(resCreate is Result.Success && resLogout is Result.Success)
    }

}