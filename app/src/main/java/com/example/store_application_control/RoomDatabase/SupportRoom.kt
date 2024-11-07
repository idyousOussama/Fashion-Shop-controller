package com.example.store_application_control.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.store_application_control.ApplicationObjs.Accounts
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = arrayOf(Accounts::class), version = 1, exportSchema = false)
abstract class SupportRoom : RoomDatabase() {

    abstract fun supportDao(): SupportDao

    companion object {

        @Volatile
        private var INSTANCE: SupportRoom? = null
         fun getDatabase(context: Context): SupportRoom {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SupportRoom::class.java,
                    "supportDB"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}