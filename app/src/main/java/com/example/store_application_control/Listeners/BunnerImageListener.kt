package com.example.store_application_control.Listeners

import android.view.View
import android.widget.ImageView
import com.example.store_application_control.ApplicationObjs.Bunner

interface BunnerImageListener {
    fun nBunnerImageLestener(bunnerImagesList: ArrayList<String> ,index :Int ,imageView: ImageView)
}