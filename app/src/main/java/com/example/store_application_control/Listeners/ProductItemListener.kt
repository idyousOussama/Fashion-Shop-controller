package com.example.store_application_control.Listeners

import android.view.View
import com.example.store_application_control.ApplicationObjs.Product


interface ProductItemListener {
fun onItemLongClicked (product : Product ,itemView : View)
}