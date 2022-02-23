package com.example.chat_data.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.chat_data.data.datasource.ChatDate
import com.example.chat_data.data.datasource.ChatMedia
import com.example.chat_data.data.datasource.ChatType
import com.example.chat_data.app.room.ChatTypeConverter
import kotlinx.serialization.Serializable

@Entity(tableName = "chats")
@Serializable
@TypeConverters(ChatTypeConverter::class)
data class Chat(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "chat_id") val chatId: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "type") val type: ChatType,
    @ColumnInfo(name = "message") val message: String? = null,
    @ColumnInfo(name = "media") val media: ChatMedia? = null,
    @ColumnInfo(name = "date") val date : ChatDate,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "success") val success: Boolean = false
)