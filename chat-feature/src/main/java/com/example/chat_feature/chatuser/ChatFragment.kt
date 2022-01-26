package com.example.chat_feature.chatuser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.basefeature.BaseFragment
import com.example.basefeature.showToast
import com.example.basefeature.visible
import com.example.basefeature.visibleIf
import com.example.chat_feature.ChatHomeViewModel
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatBinding
import com.example.media_data.MediaSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private val viewModel by hiltNavGraphViewModels<ChatHomeViewModel>(R.id.chat_nav)
    private val adapter by lazy { ChatUserListAdapter() }
    private val args by navArgs<ChatFragmentArgs>()

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    viewModel.createAndSetImageFile(
                        result.data?.data!!,
                        requireActivity().contentResolver
                    )
                }
            }
        }

    override val getBindingInflation: (LayoutInflater) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerView?.adapter = adapter

        lifecycleScope.launch {
            viewModel.subscribeToUserChat(args.userName ?: "")
        }

        viewModel.chatUserUiStateLiveData
            .map { it.chatList }
            .distinctUntilChanged()
            .observe(this) {
                adapter.submitList(it) {
                    if (it.isNotEmpty())
                        binding?.recyclerView?.scrollToPosition(it.size - 1)
                }
            }

        viewModel.chatUserUiStateLiveData
            .map { it.chatSentSuccess }
            .observe(this) {
                binding?.etMessage?.text?.clear()
            }

        viewModel.chatUserUiStateLiveData
            .map { it.mediaSource as? MediaSource.File.ImageFile }
            .distinctUntilChanged().observe(this) {
                binding?.ivAttachPrev?.visibleIf(it != null)
                binding?.ivRemoveAttach?.visibleIf(it != null)
                binding?.ivAttachPrev?.setImageURI(it?.file?.toUri())
            }

        binding?.fabSent?.setOnClickListener {
            viewModel.sendChat(args.userName ?: "", binding?.etMessage?.text?.toString() ?: "")
        }

        binding?.ivAttach?.setOnClickListener {
            openFile()
        }

        binding?.ivRemoveAttach?.setOnClickListener {
            viewModel.removeAttachment()
        }

    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        resultLauncher.launch(intent)
    }
}