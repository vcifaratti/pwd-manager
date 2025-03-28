package com.poliba.pwdmanagerpolibaproject.di

import android.app.Application
import androidx.room.Room
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordDatabase
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
    fun providePasswordDatabase(app: Application): PasswordDatabase {
        return Room.databaseBuilder(
            app,
            PasswordDatabase::class.java,
            "passwords.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePasswordDao(db: PasswordDatabase) = db.dao
} 