package com.example.auth_feature.auth_viemodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.NoSuchUserException
import com.example.auth.UserAlreadyExitsException
import com.example.auth.repo.FirebaseDataRepository
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.auth_feature.AuthViewModel
import com.example.auth_feature.MainDispatcherRule
import com.example.auth_feature.data.FakeDataProvider
import com.example.auth_feature.data.FakeFirebaseDataSource
import com.example.auth_feature.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewmodel: AuthViewModel

    @Before
    @After
    fun initialize() {
        val repo = FakeDataProvider.initDataAndRepo()
        val firebaseRepo = FirebaseDataRepository(FakeFirebaseDataSource())
        val loginUseCase = LoginUseCase(repo, firebaseRepo)
        val signUpUseCase = SignUpUseCase(repo, firebaseRepo)
        viewmodel = AuthViewModel(
            loginUseCase = loginUseCase,
            signUpUseCase = signUpUseCase
        )
    }


    @Test
    fun signUpUser_newUserSuccess() = runBlockingTest {

        viewmodel.signUpUser(FakeDataProvider.myUser)

        val signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()

        assert(signUpUiState.isSuccess)
        assert(signUpUiState.error.isNullOrEmpty())

        //mainDispatcherRule.resumeDispatcher()

        //signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()


        //assert(!signUpUiState.isLoading)
        //assert(signUpUiState.isSuccess)
    }

    @Test
    fun signUpUser_OldUserFail() = runBlockingTest {

        viewmodel.signUpUser(FakeDataProvider.user1)

        val signUpUiState = viewmodel.signUpScreenUiStateLiveData.getOrAwaitValue()

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error == UserAlreadyExitsException().message)

    }

    @Test
    fun loginUser_success() = runBlockingTest {

        viewmodel.signInUser(FakeDataProvider.user1.userName, FakeDataProvider.user1.password ?: "")

        val signUpUiState = viewmodel.signInScreenUiStateLiveData.getOrAwaitValue()

        assert(signUpUiState.isSuccess)
        assert(signUpUiState.error.isNullOrEmpty())

    }

    @Test
    fun loginUser_noSuchUser() = runBlockingTest {

        viewmodel.signInUser(
            FakeDataProvider.myUser.userName,
            FakeDataProvider.myUser.password ?: ""
        )

        val signUpUiState = viewmodel.signInScreenUiStateLiveData.getOrAwaitValue()

        assert(!signUpUiState.isSuccess)
        assert(signUpUiState.error != null && signUpUiState.error == NoSuchUserException().message)

    }

}