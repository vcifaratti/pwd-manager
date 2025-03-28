package com.poliba.pwdmanagerpolibaproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PasswordEntity::class],
    version = 1
)
abstract class PasswordDatabase : RoomDatabase() {
    abstract val dao: PasswordDao
} 