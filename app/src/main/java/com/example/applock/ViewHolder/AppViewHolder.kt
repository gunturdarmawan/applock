package com.example.applock.ViewHolder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.applock.INterface.AppOnClickListener
import com.example.applock.R

class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var icon_app: ImageView
    @JvmField
    var lock_app: ImageView
    @JvmField
    var name_app: TextView
    private var listener: AppOnClickListener? = null
    fun setListener(listener: AppOnClickListener?) {
        this.listener = listener
    }

    init {
        icon_app = itemView.findViewById(R.id.icon_app)
        lock_app = itemView.findViewById(R.id.lock_app)
        name_app = itemView.findViewById(R.id.name_app)
        itemView.setOnClickListener(View.OnClickListener {
            listener!!.selectApp(
                adapterPosition
            )
        })
    }
}