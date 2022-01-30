package com.example.chat_data.datasource

import kotlinx.serialization.Serializable
import java.time.*
import java.time.format.DateTimeFormatter

@Serializable
data class ChatDate(
    val date: Int,
    val month: Month,
    val year: Int,
    val hour: Int,
    val minute: Int,
) {

    fun isSameDay(other: ChatDate): Boolean {
        return date == other.date && month == other.month && year == other.year
    }

    fun getDateString(): String {
        val nowDate =
            Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        val prevDate = nowDate.minusDays(1)
        return when {
            nowDate.dayOfMonth == date && nowDate.month == month && nowDate.year == year -> "Today"
            prevDate.dayOfMonth == date && prevDate.month == month && prevDate.year == year -> "Yesterday"
            else -> {
                val localDate = LocalDate.of(year, month, date)
                DateTimeFormatter.ofPattern("dd MMM yyyy").withZone(ZoneId.systemDefault())
                    .format(localDate)

            }
        }
    }

    fun getTimeString(): String {
        val localDate = LocalTime.of(hour, minute)
        return DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
            .format(localDate)
    }

}


fun Long.getChatDate(): ChatDate {
    val localTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return ChatDate(
        date = localTime.dayOfMonth,
        month = localTime.month,
        year = localTime.year,
        hour = localTime.hour,
        minute = localTime.minute
    )
}