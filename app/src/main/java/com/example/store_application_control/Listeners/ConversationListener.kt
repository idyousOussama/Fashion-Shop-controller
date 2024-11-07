package com.example.store_application_control.Listeners

import com.example.store_application.AppliactionObjs.User

interface ConversationListener {
    fun onConversationListEmpty()
    fun onConversationItemClicked(rceiver : User)
}