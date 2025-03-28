package com.poliba.pwdmanagerpolibaproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PasswordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: PasswordDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL("""
                    CREATE TABLE passwords_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        username TEXT NOT NULL,
                        encryptedPassword TEXT NOT NULL,
                        url TEXT,
                        notes TEXT
                    )
                """.trimIndent())

                // Copy data from the old table to the new one
                database.execSQL("""
                    INSERT INTO passwords_new (id, title, username, encryptedPassword, url, notes)
                    SELECT id, title, username, password, url, notes
                    FROM passwords
                """.trimIndent())

                // Drop the old table
                database.execSQL("DROP TABLE passwords")

                // Rename the new table to the original name
                database.execSQL("ALTER TABLE passwords_new RENAME TO passwords")
            }
        }
    }
} 