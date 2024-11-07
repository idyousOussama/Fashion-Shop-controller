package com.example.store_application_control.RoomDatabase

import android.app.Application
import com.example.store_application_control.ApplicationObjs.Accounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

class SupportRepository (application : Application){

    val supportRoom = SupportRoom.getDatabase(application)
var  supportDao = supportRoom.supportDao()


suspend fun insertNewAccount (newAccount :Accounts){
    withContext(Dispatchers.IO){
        supportDao.insertNewAccount(newAccount)
    }
}
suspend fun getSupportAccount() : Accounts{
   return withContext(Dispatchers.IO){
        supportDao.getSupportAccount()
    }
}
    suspend fun deleteSupportAccount(){
         withContext(Dispatchers.IO){
            supportDao.deleteSupportAccount()
        }
    }

}