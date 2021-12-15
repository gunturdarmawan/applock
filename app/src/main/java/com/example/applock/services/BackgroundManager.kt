package com.example.applock.services

import com.example.applock.services.ServiceAppLockJObIntent.Companion.enqueueWork
import com.example.applock.services.BackgroundManager
import android.app.ActivityManager
import android.os.Build
import com.example.applock.services.ServiceAppLockJObIntent
import android.content.Intent
import com.example.applock.services.ServiceApplock
import com.example.applock.broadcast.RestartServiceWhenStoped
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context

class BackgroundManager {
    private var context: Context? = null
    fun init(c: Context?): BackgroundManager {
        context = c
        return this
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == serviceInfo.service.className) {
                return true
            }
        }
        return false
    }

    fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(ServiceAppLockJObIntent::class.java)) {
                val intent = Intent(context, ServiceAppLockJObIntent::class.java)
                enqueueWork(context, intent)
            }
        } else {
            if (!isServiceRunning(ServiceApplock::class.java)) {
                context!!.startService(Intent(context, ServiceApplock::class.java))
            }
        }
    }

    fun stopService(serviceClass: Class<*>) {
        if (isServiceRunning(serviceClass)) {
            context!!.stopService(Intent(context, serviceClass))
        }
    }

    fun startAlarmManager() {
        val intent = Intent(context, RestartServiceWhenStoped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0)
        val manager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period] =
            pendingIntent
    }

    fun stopAlarm() {
        val intent = Intent(context, RestartServiceWhenStoped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0)
        val manager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)
    }

    companion object {
        private const val period = 15 * 10 //15 minutes
        private const val ALARM_ID = 159874
        @JvmStatic
        var instance: BackgroundManager? = null
            get() {
                if (field == null) field = BackgroundManager()
                return field
            }
            private set
    }
}