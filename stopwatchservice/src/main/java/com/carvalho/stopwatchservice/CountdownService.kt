package com.carvalho.stopwatchservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val channelId = "CountdownServiceChannel"
    private val notificationId = 1

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(notificationId, buildNotification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val time = intent?.getIntExtra("EXTRA_TIME", -1) ?: -1
        if (time < 0) {
            Log.e("CountdownService", "Tempo inválido recebido!")
            stopSelf()
            return START_NOT_STICKY
        }

        Log.i("CountdownService", "Serviço iniciado com tempo: $time")

        serviceScope.launch {
            delay(500)
            startCountdown(time)
        }

        return START_STICKY
    }

    private suspend fun startCountdown(seconds: Int) {
        for (index in seconds downTo 0) {
            Log.i("CountdownService", "Tempo restante: $index segundos")

            val intent = Intent(COUNTDOWN_BROADCAST).apply {
                putExtra(EXTRA_TIME_LEFT, index)
            }
            sendBroadcast(intent)

            delay(1000)
        }

        Log.i("CountdownService", "Contagem finalizada!")
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Serviço de Contagem Regressiva",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
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
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val COUNTDOWN_BROADCAST = "com.carvalho.cronometroservice.COUNTDOWN_BROADCAST"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
    }
}
