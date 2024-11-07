package com.example.store_application_control.ApplicationObjs

import com.google.firebase.database.DatabaseReference
import java.io.Serializable

class  Category(var categoryName: String = "", var categoryImage: String = "", var categoryId: String = "") :Serializable {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "")
}
