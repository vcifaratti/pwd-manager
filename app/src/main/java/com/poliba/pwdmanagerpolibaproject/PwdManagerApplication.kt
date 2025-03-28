package com.poliba.pwdmanagerpolibaproject

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.poliba.pwdmanagerpolibaproject.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PwdManagerApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "pwd_manager_db"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}