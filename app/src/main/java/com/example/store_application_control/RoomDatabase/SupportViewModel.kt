package com.example.store_application_control.RoomDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.store_application_control.ApplicationObjs.Accounts


class SupportViewModel(application: Application) :AndroidViewModel(application) {
val supportRepo = SupportRepository(application )


    suspend fun insertNewAccount (newAccount : Accounts){
        supportRepo.insertNewAccount(newAccount)

    }
    suspend fun getSupportAccount() : Accounts {
         return supportRepo.getSupportAccount()

    }
    suspend fun deleteSupportAccount(){
        supportRepo.deleteSupportAccount()

    }

}