package com.example.store_application_control.ApplicationObjs

import java.io.Serializable

data class Message(var messageId :String ,var messageText :String , var senderId :String ) :
    Serializable {
    constructor() : this("","","",)
}
