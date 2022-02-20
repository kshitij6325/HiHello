package com.example.chat_feature.chatuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basefeature.*
import com.example.chat_feature.chathome.ChatHomeViewModel
import com.example.chat_feature.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : MediaBaseFragment<FragmentChatBinding>() {

    private val viewModel by viewModels<ChatViewModel>()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.init(this, args.userName ?: "")
        }

        viewModel.chatUserUiStateLiveData
            .map { it.chatList }
            .onEach {
                adapter.submitList(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)


        viewModel.chatUserUiStateLiveData
            .map { it.newChatAdded }
            .debounce(200)
            .onEach {
                it?.let {
                    binding?.recyclerView?.scrollToPosition(adapter.itemCount - 1)
                    binding?.etMessage?.text?.clear()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.chatUserUiStateLiveData
            .map { it.chatSentSuccess }
            .onEach {
                binding?.etMessage?.text?.clear()
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.chatUserUiStateLiveData
            .map { it.mediaSource }
            .onEach {
                binding?.ivAttachPrev?.visibleIf(it != null)
                binding?.ivRemoveAttach?.visibleIf(it != null)
                binding?.ivAttachPrev?.setImageURI(it?.file?.toUri())
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding?.fabSent?.setOnClickListener {
            viewModel.sendChat(args.userName ?: "", binding?.etMessage?.text?.toString() ?: "")
        }

        binding?.ivAttach?.setOnClickListener {
            openFilePicker()
        }

        binding?.ivRemoveAttach?.setOnClickListener {
            viewModel.removeAttachment()
        }

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition =
                    (binding?.recyclerView?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                lifecycleScope.launch {
                    viewModel.fetchMore(dy, firstVisibleItemPosition)
                }
            }

        })

    }


}