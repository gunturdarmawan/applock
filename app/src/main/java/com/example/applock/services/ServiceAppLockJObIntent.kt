package com.example.applock.services

import android.content.Context
import androidx.core.app.JobIntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.applock.broadcast.ReceiverApplock

class ServiceAppLockJObIntent : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        runApplock()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onDestroy()
    }

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

    companion object {
        private const val JOB_ID = 15462
        @JvmStatic
        fun enqueueWork(context: Context?, work: Intent?) {
            enqueueWork(context!!, ServiceAppLockJObIntent::class.java, JOB_ID, work!!)
        }
    }
}