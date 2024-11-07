package com.example.store_application_control.Listeners

import android.view.View
import androidx.appcompat.view.menu.MenuView.ItemView
import com.example.store_application_control.ApplicationObjs.Product

interface SearchItemListener {
    fun onItemLongClicked(product : Product , itemView : View)
}