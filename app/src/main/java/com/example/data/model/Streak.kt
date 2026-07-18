package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class Streak(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val lastSuccessTimestamp: Long = 0L
)
