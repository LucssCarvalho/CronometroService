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
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification(0))

        val time = intent?.getIntExtra("EXTRA_TIME", 0) ?: -1
        Log.i("CountdownService", "Serviço iniciado com tempo: $time")

        if (time < 0) {
            Log.e("CountdownService", "Tempo inválido recebido!")
            stopSelf()
            return START_NOT_STICKY
        }

        countdownJob = serviceScope.launch {
            delay(500)
            startCountdown(time)
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(timeLeft: Int): Notification {
        val channelId = "CountdownServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Countdown Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Contagem Regressiva")
            .setContentText("Tempo restante: $timeLeft segundos")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(timeLeft: Int) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = createNotification(timeLeft)
        notificationManager.notify(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun startCountdown(seconds: Int) {
        for (index in seconds downTo 0) {
            Log.i("CountdownService", "Tempo restante: $index segundos")

            updateNotification(index)

            val intent = Intent(COUNTDOWN_BROADCAST).apply {
                putExtra(EXTRA_TIME_LEFT, index)
            }
            sendBroadcast(intent)

            delay(1000)
        }

        Log.i("CountdownService", "Contagem finalizada!")
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val COUNTDOWN_BROADCAST = "com.carvalho.cronometroservice.COUNTDOWN_BROADCAST"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"

        @Volatile
        private var isRunning = false

        private var countdownJob: Job? = null
    }
}
