package com.example.store_application_control.ApplicationObjs

import com.example.store_application.AppliactionObjs.User

data class Conversation(var user: User?, var lastMessage: String?, var messageCounter: Int){
    constructor() : this(null,null,0)
}
