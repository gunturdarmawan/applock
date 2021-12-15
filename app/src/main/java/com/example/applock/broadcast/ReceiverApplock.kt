package com.example.applock.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.applock.PatternLockAct
import com.example.applock.utils.Utils

class ReceiverApplock : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val utils = Utils(context)
        val appRunning = utils.launcherTopApp
        if (utils.isLock(appRunning)) {
            if (appRunning != utils.lastApp) {
                utils.clearLastApp()
                utils.lastApp = appRunning
                val i = Intent(context, PatternLockAct::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.putExtra("Broadcast_receiver", "Broadcast_receiver")
                context.startActivity(i)
            }
        }
    }
}