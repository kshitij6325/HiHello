package com.example.chat_feature.chathome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.bumptech.glide.Glide
import com.example.basefeature.BaseFragment
import com.example.basefeature.gone
import com.example.basefeature.showToast
import com.example.basefeature.visible
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChatHomeFragment : BaseFragment<FragmentChatHomeBinding>() {

    private val viewModel by hiltNavGraphViewModels<ChatHomeViewModel>(R.id.chat_nav)
    private val adapter by lazy {
        ChatHomeListAdapter {
            navigate(ChatHomeFragmentDirections.actionChatHomeFragmentToChatFragment(userName = it.userName))
        }
    }

    override val getBindingInflation: (LayoutInflater) -> FragmentChatHomeBinding
        get() = FragmentChatHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        viewModel.chatHomeUiStateLiveData
            .map { it.welcomeString to it.userAvatar }
            .distinctUntilChanged()
            .onEach {
                binding?.tvWelcome?.text = it.first
                binding?.ivAvatar?.let { iv ->
                    Glide.with(requireActivity()).load(it.second).into(iv)
                }

            }.launchIn(uiScope)

        viewModel.chatHomeUiStateLiveData
            .map { it.userSyncing to it.userSyncSuccess }
            .distinctUntilChanged()
            .onEach {
                when {
                    it.first -> binding?.tvSync?.visible()
                    it.second != null -> binding?.tvSync?.gone()
                    it.second == false -> showToast("User sync failed")
                }
            }.launchIn(uiScope)

        binding?.fabCreateChat?.setOnClickListener {
            NewChatBottomSheet().show(childFragmentManager, "NewChatBottomSheet")
        }
    }

    private fun setUpRecyclerView() {
        binding?.rvHome?.adapter = adapter
        viewModel.chatHomeUiStateLiveData
            .map { it.userChatList }
            .distinctUntilChanged()
            .onEach {
                adapter.list = it
            }.launchIn(uiScope)
    }
}