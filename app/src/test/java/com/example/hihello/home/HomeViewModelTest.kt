package com.example.hihello.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.auth.usecase.GetLoggedInUserUseCase
import com.example.auth.usecase.LogoutUseCase
import com.example.hihello.MainDispatcherRule
import com.example.hihello.data.FakeDataProvider
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
        val isUserLoggedInUseCase = GetLoggedInUserUseCase(repo)
        val logoutUseCase = LogoutUseCase(repo)
        viewmodel = HomeViewModel(
            getUserLoggedInUseCase = isUserLoggedInUseCase,
        )
    }


    @Test
    fun loggedLiveDataTest() = runBlockingTest {
        viewmodel.isUserLoggedIn()
        val getHomeActivityUiState = viewmodel.homeActivityUiStateLiveData.value
        assert(getHomeActivityUiState.isLoggedIn == false)
    }

}