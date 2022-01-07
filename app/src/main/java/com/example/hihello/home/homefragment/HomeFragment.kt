package com.example.hihello.home.homefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.databinding.FragmentHomeBinding
import com.example.hihello.home.HomeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvLogOut?.setOnClickListener {
            viewModel.logOut()
        }

        launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeFragUiStateLiveData.map { it.isLoggedOut }
                    .distinctUntilChanged()
                    .collect {
                        if (it) {
                            navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
                        }
                    }
            }
        }
    }

}