package com.example.hihello.home.homeactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.databinding.FragmentHomeBinding
import com.example.hihello.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            .observe(this) { isLoggedIn ->
                isLoggedIn?.let {
                    if (it) {
                        navigate(HomeFragmentDirections.actionChatNavGraph())
                    } else {
                        navigate(HomeFragmentDirections.actionAuthNavGraph())

                    }
                }
            }


        // show error message toast when lifecycle state it at-least started
        viewModel.homeActivityUiStateLiveData
            .map { it.isLoggedInError }
            .distinctUntilChanged()
            .observe(this)
            {
                showToast(it)
            }
    }
}