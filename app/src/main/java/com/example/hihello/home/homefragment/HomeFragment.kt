package com.example.hihello.home.homefragment

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
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
        binding?.tvLogOut?.setOnClickListener {
            viewModel.logOut()
        }

        binding?.send?.setOnClickListener {
            viewModel.sendChat(user_id = binding?.etUser?.text.toString())
        }


        viewModel.homeFragUiStateLiveData.map { it.isLoggedOut }.distinctUntilChanged()
            .observe(this) {
                if (it) {
                    navigate(HomeFragmentDirections.actionHomeFragmentToAuthNav())
                }
            }

        viewModel.homeFragUiStateLiveData.map { it.currentUserChat }.distinctUntilChanged()
            .observe(this) {
                binding?.chatLl?.removeAllViews()
                it.forEach {
                    binding?.chatLl?.addView(TextView(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = it.message
                    })

                }
            }

        viewModel.homeFragUiStateLiveData.map { it.chatError }.distinctUntilChanged()
            .observe(this) {
                showToast(it)
            }
        viewModel.homeFragUiStateLiveData.map { it.chatSuccess }.distinctUntilChanged()
            .observe(this) {
                showToast(it)
            }

    }
}