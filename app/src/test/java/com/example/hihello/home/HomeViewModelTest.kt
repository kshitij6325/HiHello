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
        val logoutUseCase = LogoutUseCase(repo)
        viewmodel = HomeViewModel(
            isUserLoggedInUseCase = isUserLoggedInUseCase,
            logoutUseCase = logoutUseCase,
        )
    }


    @Test
    fun loggedLiveDataTest() = runBlockingTest {
        viewmodel.isUserLoggedIn()
        val getHomeActivityUiState = viewmodel.homeActivityUiStateLiveData.getOrAwaitValue()
        assert(!getHomeActivityUiState.isLoggedIn)
    }

}