package com.example.hihello

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.hihello.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvLogOut?.setOnClickListener {
            viewModel.logOut()
        }

        viewModel.logoutLiveData.observe(this) {
            it.onSuccess {
                navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
            }.onFailure { message ->
                showToast(message)
            }
        }
    }

}