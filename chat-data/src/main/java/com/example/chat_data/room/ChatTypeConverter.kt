package com.example.chat_data.room

import androidx.room.TypeConverter
import com.example.chat_data.datasource.ChatType

class ChatTypeConverter {

    @TypeConverter
    fun fromIntToChatType(value: Int): ChatType {
        return ChatType.fromInt(value)
    }

    @TypeConverter
    fun fromChatTypeToInt(chatType: ChatType): Int {
        return chatType.ordinal
    }
}