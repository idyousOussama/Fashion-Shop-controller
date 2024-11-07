package com.example.store_application.AppliactionObjs

import com.example.store_application_control.ApplicationObjs.Accounts
import java.io.Serializable

data class User( var userId : String,var userName: String,  var userSex : String , var userAccount: Accounts? ):Serializable{
    constructor() : this("","","",null)
}
