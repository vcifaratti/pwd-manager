package com.poliba.pwdmanagerpolibaproject.di

import android.app.Application
import androidx.room.Room
import com.poliba.pwdmanagerpolibaproject.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePasswordDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "passwords.db"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun providePasswordDao(db: AppDatabase) = db.dao
} 