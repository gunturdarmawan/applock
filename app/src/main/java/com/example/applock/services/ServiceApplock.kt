package com.example.applock.services

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.applock.broadcast.ReceiverApplock

class ServiceApplock : IntentService("ServiceApplock") {
    private fun runApplock() {
        val endTime = System.currentTimeMillis() + 210
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, ReceiverApplock::class.java)
                    sendBroadcast(intent)
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            // This method will be executed once the timer is over
                        },
                        1000 // value in milliseconds
                    )
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runApplock()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onHandleIntent(intent: Intent?) {}
    override fun onDestroy() {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onDestroy()
    }
}