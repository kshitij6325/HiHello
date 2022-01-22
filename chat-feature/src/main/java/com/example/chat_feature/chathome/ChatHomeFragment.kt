package com.example.chat_feature.chathome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.example.basefeature.BaseFragment
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatHomeFragment : BaseFragment<FragmentChatHomeBinding>() {

    private val viewModel by viewModels<ChatHomeViewModel>()
    private val adapter by lazy { ChatHomeListAdapter() }

    override val getBindingInflation: (LayoutInflater) -> FragmentChatHomeBinding
        get() = FragmentChatHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        viewModel.fetchUserChats()
        viewModel.fetchWelcomeMessage()

        viewModel.chatHomeUiStateLiveData.map { it.welcomeString }.distinctUntilChanged()
            .observe(this) {
                binding?.tvWelcome?.text = it
            }
    }

    private fun setUpRecyclerView() {
        binding?.rvHome?.adapter = adapter
        viewModel.chatHomeUiStateLiveData.map { it.userChatList }.distinctUntilChanged()
            .observe(this) {
                adapter.list = it
            }
    }
}