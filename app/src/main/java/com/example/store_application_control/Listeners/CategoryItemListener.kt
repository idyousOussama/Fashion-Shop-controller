package com.example.store_application_control.Listeners

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import com.example.store_application_control.ApplicationObjs.Category

interface CategoryItemListener {
    fun onItemViewClicked(lastIteViewText :TextView,category : Category)
    fun onCtegoryItemLongClicked(itemView :View,category : Category)
}