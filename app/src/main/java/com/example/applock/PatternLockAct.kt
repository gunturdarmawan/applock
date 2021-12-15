package com.example.applock

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.PatternLockView.Dot
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.example.applock.model.Password
import com.example.applock.services.BackgroundManager.Companion.instance
import com.example.applock.utils.Utils
import com.shuhart.stepview.StepView

class PatternLockAct : AppCompatActivity() {
    var stepView = findViewById<StepView>(R.id.step_view)
    var normalLayout = findViewById<LinearLayout>(R.id.normal_layout)
    var relativeLayout = findViewById<RelativeLayout>(R.id.root)
    var status_password = findViewById<TextView>(R.id.status_password)
    var utilsPassword = Password(this)
    var userPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern_lock)
        instance!!.init(this).startService()
        initIconApp()
        initLayout()
        initPatternListener()
    }

    private fun initIconApp() {
        if (intent.getStringExtra("Broadcast_receiver") != null) {
            val icone = findViewById<ImageView>(R.id.app_icone)
            val current_app = Utils(this).lastApp
            var applicationInfo: ApplicationInfo? = null
            try {
                applicationInfo = packageManager.getApplicationInfo(current_app, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            icone.setImageDrawable(applicationInfo!!.loadIcon(packageManager))
        }
    }

    private fun initLayout() {

            status_password.setText(utilsPassword.STATUS_FIRST_STEP);
        if (utilsPassword!!.password == null) {
            normalLayout.setVisibility(View.GONE)
            stepView.setVisibility(View.VISIBLE)
            stepView.setStepsNumber(2)
            stepView.go(0, true)
        } else {
            normalLayout.setVisibility(View.VISIBLE)
            stepView.setVisibility(View.GONE)
            val backColor = ResourcesCompat.getColor(resources, R.color.purple_500, null)
            relativeLayout.setBackgroundColor(backColor)
            status_password.setTextColor(Color.WHITE)
        }
    }

    private fun initPatternListener() {
        val patternLockView = findViewById<PatternLockView>(R.id.pattern_view)
        patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<Dot>) {}
            override fun onComplete(pattern: List<Dot>) {
                val pwd = PatternLockUtils.patternToString(patternLockView, pattern)
                if (pwd.length < 4) {
                    status_password.setText(utilsPassword.SHEMA_FAILED);
                    patternLockView.clearPattern()
                    return
                }
                if (utilsPassword!!.password == null) {
                    if (utilsPassword!!.isFirstStep) {
                        userPassword = pwd
                        utilsPassword!!.isFirstStep = false
                        status_password.setText((utilsPassword.STATUS_NEXT_STEP));
                        stepView!!.go(1, true)
                    } else {
                        if (userPassword == pwd) {
                            utilsPassword!!.password = userPassword.toString()
                            status_password.setText(utilsPassword.STATUS_PASSWORD_CORRECT);
                            stepView!!.done(true)
                            startAct()
                        } else {
                            status_password.setText((utilsPassword.STATUS_PASSWORD_INCORRECT));
                        }
                    }
                } else {
                    if (utilsPassword!!.isCorrect(pwd)) {
                        status_password.setText(utilsPassword.STATUS_PASSWORD_CORRECT);
                        startAct()
                    } else {
                        status_password.setText(utilsPassword.STATUS_PASSWORD_INCORRECT);
                    }
                }
                patternLockView.clearPattern()
            }

            override fun onCleared() {}
        })
    }

    private fun startAct() {
        if (intent.getStringExtra("Broadcast_receiver") == null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    override fun onBackPressed() {
        if (utilsPassword!!.password == null && !utilsPassword!!.isFirstStep) {
            stepView!!.go(0, true)
            utilsPassword!!.isFirstStep = true
            status_password.setText(utilsPassword.STATUS_FIRST_STEP);
        } else {
            startCurrentHomePackage()
            finish()
            super.onBackPressed()
        }
    }

    private fun startCurrentHomePackage() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val activityInfo = resolveInfo!!.activityInfo
        val componentName =
            ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
    }
}