package com.example.chat_data.room

import androidx.room.TypeConverter
import com.example.chat_data.datasource.ChatDate
import com.example.chat_data.datasource.ChatMedia
import com.example.chat_data.datasource.ChatType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatTypeConverter {

    @TypeConverter
    fun fromIntToChatType(value: Int): ChatType {
        return ChatType.fromInt(value)
    }

    @TypeConverter
    fun fromChatTypeToInt(chatType: ChatType): Int {
        return chatType.ordinal
    }

    @TypeConverter
    fun fromChatMedia(value: ChatMedia?): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toChatMedia(string: String): ChatMedia? {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun fromDate(value: ChatDate): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDate(string: String): ChatDate {
        return Json.decodeFromString(string)
    }
}