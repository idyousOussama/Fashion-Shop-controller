package com.example.store_application_control.ApplicationObjs

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable
@Entity(tableName = "account")
class Accounts(
  var email: String,
  @PrimaryKey
  var accountuserId :String,
  var accountProfile: String,
  var accountPassword: String,
  var accountName: String,
):Serializable{
  constructor() : this("","","","" ,"")
}
