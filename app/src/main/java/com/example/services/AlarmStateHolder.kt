package com.example.services

import kotlinx.coroutines.flow.MutableStateFlow

object AlarmStateHolder {
    val isRinging = MutableStateFlow(false)
    val ringingAlarmId = MutableStateFlow<Int?>(null)
    val ringingSurahId = MutableStateFlow<Int?>(null)
    val ringingAyahNumber = MutableStateFlow<Int?>(null)
    val isAudioMuted = MutableStateFlow(false)
    val remainingSeconds = MutableStateFlow(60)

    fun triggerAlarm(alarmId: Int, surahId: Int, ayahNumber: Int) {
        ringingAlarmId.value = alarmId
        ringingSurahId.value = surahId
        ringingAyahNumber.value = ayahNumber
        isRinging.value = true
        isAudioMuted.value = false
        remainingSeconds.value = 60
    }

    fun clear() {
        isRinging.value = false
        ringingAlarmId.value = null
        ringingSurahId.value = null
        ringingAyahNumber.value = null
        isAudioMuted.value = false
        remainingSeconds.value = 60
    }
}
