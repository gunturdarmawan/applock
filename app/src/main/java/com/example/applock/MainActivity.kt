package com.example.applock

import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.os.Bundle
import com.example.applock.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applock.adapter.AppAdapter
import com.example.applock.model.AppItem
import android.content.pm.PackageManager
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.applock.utils.Utils
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    var layout_permission: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initView()
    }

    private fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view_app)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val appAdapter = AppAdapter(this, allApps)
        recyclerView.adapter = appAdapter
        layout_permission = findViewById(R.id.layout_permission)
    }

    private val allApps: List<AppItem>
        private get() {
            val results: MutableList<AppItem> = ArrayList()
            val pk = packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfoList = pk.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfoList) {
                val activityInfo = resolveInfo.activityInfo
                results.add(
                    AppItem(
                        activityInfo.loadIcon(pk),
                        activityInfo.loadLabel(pk).toString(),
                        activityInfo.packageName
                    )
                )
            }
            return results
        }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar!!.title = "App List"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) finish()
        return true
    }

    fun setPermission(view: View?) {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Utils.checkPermission(this)) {
                layout_permission!!.visibility = View.GONE
            } else {
                layout_permission!!.visibility = View.VISIBLE
            }
        }
        super.onResume()
    }
}