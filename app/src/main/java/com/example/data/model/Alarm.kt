package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val isActive: Boolean = true,
    val daysSelected: String = "", // e.g., "Mon,Tue,Wed,Thu,Fri,Sat,Sun"
    val surahId: Int,
    val ayahNumber: Int
) {
    val formattedTime: String
        get() {
            val isPm = hour >= 12
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val displayMinute = String.format("%02d", minute)
            val amPm = if (isPm) "PM" else "AM"
            return "$displayHour:$displayMinute $amPm"
        }
}
