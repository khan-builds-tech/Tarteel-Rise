package com.example.data.repository

import com.example.data.dao.AlarmDao
import com.example.data.dao.StreakDao
import com.example.data.model.Alarm
import com.example.data.model.Streak
import kotlinx.coroutines.flow.Flow

class AlarmRepository(
    private val alarmDao: AlarmDao,
    private val streakDao: StreakDao
) {
    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()
    val streak: Flow<Streak?> = streakDao.getStreak()

    suspend fun getAlarmById(id: Int): Alarm? = alarmDao.getAlarmById(id)

    suspend fun insertAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)

    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    suspend fun deleteAlarmById(id: Int) = alarmDao.deleteAlarmById(id)

    suspend fun getStreakDirect(): Streak? = streakDao.getStreakDirect()

    suspend fun insertOrUpdateStreak(streak: Streak) = streakDao.insertOrUpdateStreak(streak)
}
