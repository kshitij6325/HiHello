package com.example.chat_data.datasource

enum class ChatType {
    SENT,
    RECEIVED;

    companion object {
        fun fromInt(type: Int): ChatType = when (type) {
            0 -> SENT
            1 -> RECEIVED
            else -> SENT
        }
    }
}

