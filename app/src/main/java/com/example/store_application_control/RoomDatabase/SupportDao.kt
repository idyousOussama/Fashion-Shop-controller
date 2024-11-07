package com.example.store_application_control.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.store_application_control.ApplicationObjs.Accounts
@Dao
interface SupportDao {

    @Insert
fun insertNewAccount(newAccount : Accounts)
 @Query("Select * FROM account")
 fun getSupportAccount() : Accounts
 @Query("Delete From account")
 fun deleteSupportAccount()

}