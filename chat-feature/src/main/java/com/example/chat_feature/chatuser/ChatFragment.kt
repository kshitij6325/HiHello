package com.example.chat_feature.chatuser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.basefeature.BaseFragment
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private val viewModel by hiltNavGraphViewModels<ChatHomeViewModel>(R.id.chat_nav)
    private val adapter by lazy { ChatUserListAdapter() }
    private val args by navArgs<ChatFragmentArgs>()

    override val getBindingInflation: (LayoutInflater) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.recyclerView?.adapter = adapter
        viewModel.subscribeToUserChat(args.userName ?: "")

        viewModel.chatUserUiStateLiveData
            .map { it.chatList }
            .distinctUntilChanged()
            .observe(this) {
                adapter.submitList(it) {
                    if (it.isNotEmpty())
                        binding?.recyclerView?.smoothScrollToPosition(it.size - 1)
                }
            }

        viewModel.chatUserUiStateLiveData
            .map { it.chatSentSuccess }
            .observeForever {
                binding?.etMessage?.text?.clear()
            }

        binding?.fabSent?.setOnClickListener {
            viewModel.sendChat(args.userName ?: "", binding?.etMessage?.text?.toString() ?: "")
        }
    }

}