package com.example.store_application_control.ApplicationObjs

import android.net.Uri
import java.io.Serializable

class Bunner(var id: String = "", var bunnerImage: String  = "") : Serializable{
    constructor() : this("", "")

}
