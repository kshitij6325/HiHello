package com.example.chat_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.chat_data.datasource.ChatType
import com.example.chat_data.room.ChatTypeConverter
import kotlinx.serialization.Serializable

@Entity(tableName = "chats")
@Serializable
@TypeConverters(ChatTypeConverter::class)
data class Chat(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "chat_id") val chatId: Long? = null,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "type") val type: ChatType,
    @ColumnInfo(name = "message") val message: String? = null,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "success") val success: Boolean = false
)