package com.example.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var fallbackJob: Job? = null

    companion object {
        const val CHANNEL_ID = "tarteel_alarm_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_MUTE = "com.example.services.ACTION_MUTE"
        const val ACTION_DISMISS = "com.example.services.ACTION_DISMISS"
    }

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_MUTE -> {
                muteAlarmAndStartSafetyLoop()
            }
            ACTION_DISMISS -> {
                stopSelf()
            }
            else -> {
                val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
                val surahId = intent?.getIntExtra("SURAH_ID", 1) ?: 1
                val ayahNumber = intent?.getIntExtra("AYAH_NUMBER", 1) ?: 1

                AlarmStateHolder.triggerAlarm(alarmId, surahId, ayahNumber)
                startForeground(NOTIFICATION_ID, buildNotification(surahId, ayahNumber))
                playRingtone()
                
                // Bring MainActivity to the foreground
                try {
                    val mainIntent = Intent(this, MainActivity::class.java).apply {
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("FROM_ALARM", true)
                    }
                    startActivity(mainIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun playRingtone() {
        if (mediaPlayer != null) return
        try {
            var alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, alertUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun muteAlarmAndStartSafetyLoop() {
        try {
            mediaPlayer?.setVolume(0.0f, 0.0f)
            AlarmStateHolder.isAudioMuted.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fallbackJob?.cancel()
        fallbackJob = serviceScope.launch {
            AlarmStateHolder.remainingSeconds.value = 60
            while (AlarmStateHolder.remainingSeconds.value > 0) {
                delay(1000L)
                AlarmStateHolder.remainingSeconds.value -= 1
            }
            
            // 60-second safety timeout expired without reaching 80%! Resume audio at max volume.
            try {
                mediaPlayer?.setVolume(1.0f, 1.0f)
                AlarmStateHolder.isAudioMuted.value = false
                playRingtone()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TarteelRise::AlarmWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quranic Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Triggers the Tarteel Rise recitation screen."
                setSound(null, null)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(surahId: Int, ayahNumber: Int): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FROM_ALARM", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tarteel Rise: Time to Recite")
            .setContentText("Recitation required to dismiss alarm.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        fallbackJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }

        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        AlarmStateHolder.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
