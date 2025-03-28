package com.poliba.pwdmanagerpolibaproject.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY title ASC")
    fun getAllPasswords(): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Int): PasswordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)

    @Update
    suspend fun updatePassword(password: PasswordEntity)

    @Delete
    suspend fun deletePassword(password: PasswordEntity)

    @Query("DELETE FROM passwords")
    suspend fun deleteAllPasswords()

    @Query("SELECT * FROM passwords WHERE title LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchPasswords(query: String): Flow<List<PasswordEntity>>
} 