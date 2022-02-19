package com.example.chat_feature.chathome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.basefeature.BaseFragment
import com.example.basefeature.gone
import com.example.basefeature.showToast
import com.example.basefeature.visible
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

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
            .onEach {
                binding?.tvWelcome?.text = it.first
                binding?.ivAvatar?.let { iv ->
                    Glide.with(requireActivity()).load(it.second).into(iv)
                }

            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.chatHomeUiStateLiveData
            .map { it.userSyncing to it.userSyncSuccess }
            .distinctUntilChanged()
            .onEach {
                when {
                    it.first -> binding?.tvSync?.visible()
                    it.second != null -> binding?.tvSync?.gone()
                    it.second == false -> showToast("User sync failed")
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding?.fabCreateChat?.setOnClickListener {
            NewChatBottomSheet().show(childFragmentManager, "NewChatBottomSheet")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Desoryy home", "Des")
    }

    private fun setUpRecyclerView() {
        binding?.rvHome?.adapter = adapter
        viewModel.chatHomeUiStateLiveData
            .map { it.userChatList }
            .onEach {
                adapter.list = it
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}