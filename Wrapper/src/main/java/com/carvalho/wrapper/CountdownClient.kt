package com.carvalho.wrapper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log

class CountdownClient(private val context: Context) {
    private var messenger: Messenger? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("CountdownClient", "Conectado ao CountdownService")
            messenger = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("CountdownClient", "Desconectado do CountdownService")
            isBound = false
        }
    }

    fun bindService() {
        Log.d("CountdownClient", "Binding com service")
        val intent = Intent("com.carvalho.stopwatchservice.COUNTDOWN_SERVICE").apply {
            setPackage("com.carvalho.stopwatchservice")
        }

        Log.d("CountdownClient", "intent: ${intent}")

        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }

    fun startCountdown(time: Int) {
        if (!isBound) return
        val msg = Message.obtain(null, START_COUNTDOWN, time, 0)
        messenger?.send(msg)
    }

    fun pauseCountdown() {
        if (!isBound) return
        val msg = Message.obtain(null, PAUSE_COUNTDOWN)
        messenger?.send(msg)
    }

    fun resetCountdown() {
        if (!isBound) return
        val msg = Message.obtain(null, RESET_COUNTDOWN)
        messenger?.send(msg)
    }

    companion object {
        const val START_COUNTDOWN = 1
        const val PAUSE_COUNTDOWN = 2
        const val RESET_COUNTDOWN = 3
    }
}
