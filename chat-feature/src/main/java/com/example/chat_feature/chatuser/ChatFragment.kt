package com.example.chat_feature.chatuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.basefeature.BaseFragment
import com.example.chat_feature.R
import com.example.chat_feature.databinding.FragmentChatBinding

class ChatFragment : BaseFragment<FragmentChatBinding>() {

    override val getBindingInflation: (LayoutInflater) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

}