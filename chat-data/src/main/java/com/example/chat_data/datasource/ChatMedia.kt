package com.example.chat_data.datasource

import com.example.media_data.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class ChatMedia(val url: String? = null, val localPath: String? = null, val type: MediaType)