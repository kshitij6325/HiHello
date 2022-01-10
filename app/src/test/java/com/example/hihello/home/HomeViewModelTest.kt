package com.example.hihello.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.NoSuchUserException
import com.example.auth.UserAlreadyExitsException
import com.example.auth.usecase.IsUserLoggedInUseCase
import com.example.auth.usecase.LoginUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.auth.usecase.SignUpUseCase
import com.example.hihello.MainDispatcherRule
import com.example.hihello.data.FakeDataProvider
import com.example.hihello.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewmodel: HomeViewModel

    @Before
    @After
    fun initialize() {
        val repo = FakeDataProvider.initDataAndRepo()
        val isUserLoggedInUseCase = IsUserLoggedInUseCase(repo)
        val loginUseCase = LoginUseCase(repo)
        val logoutUseCase = LogoutUseCase(repo)
        val signUpUseCase = SignUpUseCase(repo)
        viewmodel = HomeViewModel(
            isUserLoggedInUseCase = isUserLoggedInUseCase,
            loginUseCase = loginUseCase,
            logOutUseCase = logoutUseCase,
            signUpUseCase = signUpUseCase
        )
    }


    @Test
    fun loggedLiveDataTest() = runBlockingTest {
        viewmodel.isUserLoggedIn()
        val getHomeActivityUiState = viewmodel.homeActivityUiStateLiveData.getOrAwaitValue()
        assert(!getHomeActivityUiState.isLoggedIn)
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

    @Test
    fun logoutUser_success() = runBlockingTest {

        viewmodel.signInUser(FakeDataProvider.user1.userName, FakeDataProvider.user1.password ?: "")
        val loginUIState = viewmodel.signInScreenUiStateLiveData.getOrAwaitValue()
        assert(loginUIState.isSuccess && loginUIState.error.isNullOrEmpty())

        viewmodel.logOut()
        val signUpUiState = viewmodel.homeFragUiStateLiveData.getOrAwaitValue()
        assert(signUpUiState.isLoggedOut)
        assert(signUpUiState.errorLogOut.isNullOrEmpty())

    }


}