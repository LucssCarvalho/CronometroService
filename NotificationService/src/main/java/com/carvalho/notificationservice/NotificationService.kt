package com.carvalho.notificationservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationService : Service() {

    private val channelId = "NotificationServiceChannel"
    private val notificationId = 1
    private val countdownReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeLeft = intent?.getIntExtra("EXTRA_TIME_LEFT", -1) ?: -1
            if (timeLeft >= 0) updateNotification(timeLeft)
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(notificationId, buildNotification(0))

        val filter = IntentFilter("com.carvalho.cronometroservice.COUNTDOWN_BROADCAST")
        registerReceiver(countdownReceiver, filter)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações do Cronômetro",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateNotification(timeLeft: Int) {
        val notification = buildNotification(timeLeft)
        getSystemService(NotificationManager::class.java)?.notify(notificationId, notification)
    }

    private fun buildNotification(timeLeft: Int): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Contagem Regressiva")
            .setContentText("Tempo restante: $timeLeft segundos")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        unregisterReceiver(countdownReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
