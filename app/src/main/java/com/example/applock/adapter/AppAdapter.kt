package com.example.applock.adapter

import android.content.Context
import com.example.applock.model.AppItem
import androidx.recyclerview.widget.RecyclerView
import com.example.applock.ViewHolder.AppViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.applock.R
import com.example.applock.INterface.AppOnClickListener
import com.example.applock.utils.Utils

class AppAdapter(private val mContext: Context, private val appItemList: List<AppItem>) :
    RecyclerView.Adapter<AppViewHolder>() {
    private val utils: Utils
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_apps, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.name_app.text = appItemList[position].name
        holder.icon_app.setImageDrawable(appItemList[position].icone)
        val pk = appItemList[position].packageName
        if (utils.isLock(pk)) {
            holder.lock_app.setImageResource(R.drawable.ic_baseline_lock_24)
        } else {
            holder.lock_app.setImageResource(R.drawable.ic_lock_open_black_24dp)
        }
        holder.setListener(object : AppOnClickListener {
            override fun selectApp(pos: Int) {
                if (utils.isLock(pk)) {
                    holder.lock_app.setImageResource(R.drawable.ic_lock_open_black_24dp)
                    utils.unLock(pk)
                } else {
                    holder.lock_app.setImageResource(R.drawable.ic_baseline_lock_24)
                    utils.lock(pk)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return appItemList.size
    }

    init {
        utils = Utils(mContext)
    }
}