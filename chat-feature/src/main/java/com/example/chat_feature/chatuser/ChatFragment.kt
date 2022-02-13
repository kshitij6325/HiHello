package com.example.chat_feature.chatuser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.basefeature.*
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatBinding
import com.example.media_data.MediaSource
import com.example.media_data.MediaType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : MediaBaseFragment<FragmentChatBinding>() {

    private val viewModel by hiltNavGraphViewModels<ChatHomeViewModel>(R.id.chat_nav)
    private val adapter by lazy { ChatUserListAdapter() }
    private val args by navArgs<ChatFragmentArgs>()

    override val getBindingInflation: (LayoutInflater) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    override val onFileSelected: suspend (ActivityResult) -> Unit = {
        viewModel.createAndSetImageFile(
            it.data?.data!!,
            requireActivity().contentResolver
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerView?.adapter = adapter

        lifecycleScope.launch {
            viewModel.subscribeToUserChat(args.userName ?: "")
        }

        viewModel.chatUserUiStateLiveData
            .map { it.chatList }
            .distinctUntilChanged()
            .onEach {
                adapter.submitList(it) {
                    if (it.isNotEmpty())
                        binding?.recyclerView?.scrollToPosition(it.size - 1)
                }
            }.launchIn(uiScope)

        viewModel.chatUserUiStateLiveData
            .map { it.chatSentSuccess }
            .onEach {
                binding?.etMessage?.text?.clear()
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.chatUserUiStateLiveData
            .map { it.mediaSource }
            .distinctUntilChanged()
            .onEach {
                binding?.ivAttachPrev?.visibleIf(it != null)
                binding?.ivRemoveAttach?.visibleIf(it != null)
                binding?.ivAttachPrev?.setImageURI(it?.file?.toUri())
            }.launchIn(uiScope)

        binding?.fabSent?.setOnClickListener {
            viewModel.sendChat(args.userName ?: "", binding?.etMessage?.text?.toString() ?: "")
        }

        binding?.ivAttach?.setOnClickListener {
            openFilePicker()
        }

        binding?.ivRemoveAttach?.setOnClickListener {
            viewModel.removeAttachment()
        }

    }


}