package com.example.hihello.home.homeactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.databinding.FragmentHomeBinding
import com.example.hihello.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isUserLoggedIn()
        viewModel.homeActivityUiStateLiveData
            .map { it.isLoggedIn }
            .distinctUntilChanged()
            .onEach { isLoggedIn ->
                isLoggedIn?.let {
                    if (it) {
                        navigate(HomeFragmentDirections.actionChatNavGraph())
                    } else {
                        navigate(HomeFragmentDirections.actionAuthNavGraph())

                    }
                }
            }.launchIn(uiScope)


        // show error message toast when lifecycle state it at-least started
        viewModel.homeActivityUiStateLiveData
            .map { it.isLoggedInError }
            .distinctUntilChanged()
            .onEach {
                showToast(it)
            }.launchIn(uiScope)
    }
}