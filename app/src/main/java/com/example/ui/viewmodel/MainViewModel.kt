package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.Alarm
import com.example.data.model.AyahRecord
import com.example.data.model.Streak
import com.example.data.model.Surah
import com.example.data.repository.AlarmRepository
import com.example.data.repository.QuranRepository
import com.example.core.speech.SpeechRecognizerManager
import com.example.core.utils.TextMatcher
import com.example.services.AlarmScheduler
import com.example.services.AlarmService
import com.example.services.AlarmStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(context: Context) : ViewModel() {

    private val db = AppDatabase.getDatabase(context)
    private val alarmRepository = AlarmRepository(db.alarmDao(), db.streakDao())
    private val quranRepository = QuranRepository()

    // Quran Data State
    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    // Alarms and Streak State from DB
    val alarms: StateFlow<List<Alarm>> = alarmRepository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streak: StateFlow<Streak?> = alarmRepository.streak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Speech & Recitation State
    private val _speechText = MutableStateFlow("")
    val speechText: StateFlow<String> = _speechText.asStateFlow()

    private val _matchScore = MutableStateFlow(0.0)
    val matchScore: StateFlow<Double> = _matchScore.asStateFlow()

    private val _highestScore = MutableStateFlow(0.0)
    val highestScore: StateFlow<Double> = _highestScore.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _speechError = MutableStateFlow<String?>(null)
    val speechError: StateFlow<String?> = _speechError.asStateFlow()

    private var speechRecognizerManager: SpeechRecognizerManager? = null

    // Active Ringing State
    val isRinging = AlarmStateHolder.isRinging
    val remainingSeconds = AlarmStateHolder.remainingSeconds
    val isAudioMuted = AlarmStateHolder.isAudioMuted

    private val _activeAyahText = MutableStateFlow("")
    val activeAyahText: StateFlow<String> = _activeAyahText.asStateFlow()

    private val _activeAyahTranslation = MutableStateFlow("")
    val activeAyahTranslation: StateFlow<String> = _activeAyahTranslation.asStateFlow()

    private val _activeSurahTransliteration = MutableStateFlow("")
    val activeSurahTransliteration: StateFlow<String> = _activeSurahTransliteration.asStateFlow()

    // Event flow for navigation trigger
    val navigationTrigger = MutableStateFlow<String?>(null)

    init {
        loadQuranData(context)
        observeRingingState(context)
    }

    private fun loadQuranData(context: Context) {
        viewModelScope.launch {
            val data = quranRepository.getSurahs(context)
            _surahs.value = data
        }
    }

    private fun observeRingingState(context: Context) {
        viewModelScope.launch {
            AlarmStateHolder.isRinging.collect { ringing ->
                if (ringing) {
                    val surahId = AlarmStateHolder.ringingSurahId.value ?: 1
                    val ayahNum = AlarmStateHolder.ringingAyahNumber.value ?: 1
                    
                    val surah = quranRepository.getSurahById(context, surahId)
                    val ayah = surah?.verses?.firstOrNull { it.id == ayahNum }
                    
                    _activeAyahText.value = ayah?.text ?: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                    _activeAyahTranslation.value = ayah?.translation ?: "In the name of Allah..."
                    _activeSurahTransliteration.value = surah?.transliteration ?: "Al-Fatiha"
                    
                    // Reset recitation metrics
                    _speechText.value = ""
                    _matchScore.value = 0.0
                    _highestScore.value = 0.0
                    _speechError.value = null
                }
            }
        }
    }

    // CRUD Alarm operations
    fun addAlarm(context: Context, hour: Int, minute: Int, surahId: Int, ayahNumber: Int, days: List<String>) {
        viewModelScope.launch {
            val daysString = days.joinToString(",")
            val alarm = Alarm(
                hour = hour,
                minute = minute,
                isActive = true,
                daysSelected = daysString,
                surahId = surahId,
                ayahNumber = ayahNumber
            )
            val id = alarmRepository.insertAlarm(alarm)
            val savedAlarm = alarm.copy(id = id.toInt())
            AlarmScheduler.scheduleAlarm(context, savedAlarm)
        }
    }

    fun toggleAlarmActive(context: Context, alarm: Alarm) {
        viewModelScope.launch {
            val updated = alarm.copy(isActive = !alarm.isActive)
            alarmRepository.updateAlarm(updated)
            if (updated.isActive) {
                AlarmScheduler.scheduleAlarm(context, updated)
            } else {
                AlarmScheduler.cancelAlarm(context, updated)
            }
        }
    }

    fun deleteAlarm(context: Context, alarm: Alarm) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, alarm)
            alarmRepository.deleteAlarm(alarm)
        }
    }

    // Speech Recitation mechanics
    fun startRecitation(context: Context) {
        if (_isListening.value) return

        // Send Mute action to Service
        val muteIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_MUTE
        }
        context.startService(muteIntent)

        speechRecognizerManager = SpeechRecognizerManager(
            context = context,
            onPartialResults = { partial ->
                processRecitedText(partial)
            },
            onResults = { result ->
                processRecitedText(result)
            },
            onError = { error ->
                _speechError.value = error
                _isListening.value = false
            },
            onListeningStatusChanged = { listening ->
                _isListening.value = listening
            }
        )

        _speechError.value = null
        _isListening.value = true
        speechRecognizerManager?.startListening()
    }

    fun stopRecitation() {
        speechRecognizerManager?.stopListening()
        speechRecognizerManager?.destroy()
        speechRecognizerManager = null
        _isListening.value = false
    }

    private fun processRecitedText(text: String) {
        _speechText.value = text
        val target = _activeAyahText.value
        val score = TextMatcher.calculateSimilarity(text, target)
        _matchScore.value = score
        if (score > _highestScore.value) {
            _highestScore.value = score
        }

        // Automatic Success triggers at 80% (0.80)
        if (_highestScore.value >= 0.80) {
            handleDismissalSuccess()
        }
    }

    // Manual debug submission for Emulator/Simulation safety
    fun submitManualRecitation(text: String) {
        processRecitedText(text)
    }

    private fun handleDismissalSuccess() {
        stopRecitation()
        // Clear global alarm state
        AlarmStateHolder.clear()
        // Request navigation trigger
        navigationTrigger.value = "SUCCESS"
    }

    fun completeStreakAndDismiss(context: Context) {
        viewModelScope.launch {
            // Dismiss alarm service
            val dismissIntent = Intent(context, AlarmService::class.java).apply {
                action = AlarmService.ACTION_DISMISS
            }
            context.startService(dismissIntent)

            // Increment streak count in Room DB
            incrementStreak()
            
            // Clear navigation trigger
            navigationTrigger.value = null
        }
    }

    private suspend fun incrementStreak() = withContext(Dispatchers.IO) {
        val current = alarmRepository.getStreakDirect() ?: Streak(1, 0, 0L)
        val now = System.currentTimeMillis()
        val lastSuccess = current.lastSuccessTimestamp
        
        val newStreakCount = if (lastSuccess == 0L) {
            1
        } else {
            val diffMs = now - lastSuccess
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            when {
                diffDays < 1 -> current.currentStreak // same day, do not increment twice
                diffDays < 2 -> current.currentStreak + 1 // consecutive day!
                else -> 1 // broken streak, restart at 1
            }
        }
        
        alarmRepository.insertOrUpdateStreak(
            Streak(
                id = 1,
                currentStreak = newStreakCount,
                lastSuccessTimestamp = now
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizerManager?.destroy()
    }
}
