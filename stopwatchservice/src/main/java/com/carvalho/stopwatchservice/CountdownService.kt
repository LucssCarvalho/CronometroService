package com.carvalho.stopwatchservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class CountdownService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var countdownJob: Job? = null
    private var timeLeft: Int = 0

    private val messenger = Messenger(IncomingHandler())

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification(0))
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
    private suspend fun startCountdown(timeLeft: Int) {
        for (index in this.timeLeft downTo 0) {
            Log.i("CountdownService", "Tempo restante: $index segundos")
            updateNotification(index)

            val intent = Intent(COUNTDOWN_BROADCAST).apply {
                putExtra(EXTRA_TIME_LEFT, index)
            }
            sendBroadcast(intent)

            delay(1000)
            this.timeLeft = index
        }

        Log.i("CountdownService", "Contagem finalizada!")
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            Log.d("CountdownService", "Mensagem recebida: ${msg.what}")

            when (msg.what) {
                START_COUNTDOWN -> {
                    val timeLeft = msg.arg1
                    Log.d("CountdownService", "Iniciando contagem regressiva: $timeLeft segundos")
                    countdownJob?.cancel()
                    countdownJob = serviceScope.launch { startCountdown(timeLeft) }
                }
                PAUSE_COUNTDOWN -> {
                    Log.d("CountdownService", "Pausando contagem")
                    countdownJob?.cancel()
                }
                RESET_COUNTDOWN -> {
                    Log.d("CountdownService", "Resetando contagem")
                    countdownJob?.cancel()
                    updateNotification(0)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    companion object {
        const val COUNTDOWN_BROADCAST = "com.carvalho.cronometroservice.COUNTDOWN_BROADCAST"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"

        const val START_COUNTDOWN = 1
        const val PAUSE_COUNTDOWN = 2
        const val RESET_COUNTDOWN = 3
    }
}
