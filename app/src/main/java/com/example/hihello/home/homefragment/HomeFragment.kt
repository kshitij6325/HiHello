package com.example.hihello.home.homefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.example.basefeature.BaseFragment
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
        binding?.tvLogOut?.setOnClickListener {
            viewModel.logOut()
        }

        viewModel.homeFragUiStateLiveData.observe(this) {
            if (it.isLoggedOut) {
                navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
            }
        }

    }
}