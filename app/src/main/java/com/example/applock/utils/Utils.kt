package com.example.applock.utils

import android.app.usage.UsageStatsManager
import android.app.ActivityManager
import android.os.Build
import android.app.usage.UsageEvents
import android.text.TextUtils
import androidx.annotation.RequiresApi
import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import io.paperdb.Paper

class Utils(private val context: Context) {
    private val EXTRA_LAST_APP = "EXTRA_LAST_APP"
    fun isLock(packageName: String?): Boolean {
        return Paper.book().read<Any?>(packageName) != null
    }

    fun lock(pk: String) {
        Paper.book().write(pk, pk)
    }

    fun unLock(pk: String?) {
        Paper.book().delete(pk)
    }

    var lastApp: String
        get() = Paper.book().read(EXTRA_LAST_APP)
        set(pk) {
            Paper.book().write(EXTRA_LAST_APP, pk)
        }

    fun clearLastApp() {
        Paper.book().delete(EXTRA_LAST_APP)
    }

    var usageStatsManager: UsageStatsManager? = null
    val launcherTopApp: String
        get() {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                usageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                val taskInfoList = manager.getRunningTasks(1)
                if (null != taskInfoList && !taskInfoList.isEmpty()) {
                    return taskInfoList[0].topActivity!!.packageName
                }
            } else {
                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 10000
                var result = ""
                val event = UsageEvents.Event()
                val usageEvents = usageStatsManager!!.queryEvents(beginTime, endTime)
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        result = event.packageName
                    }
                }
                if (!TextUtils.isEmpty(result)) return result
            }
            return ""
        }

    companion object {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun checkPermission(ctx: Context): Boolean {
            val appOpsManager = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                ctx.packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }
    }

    init {
        Paper.init(context)
    }
}