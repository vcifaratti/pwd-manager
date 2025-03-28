package com.poliba.pwdmanagerpolibaproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.poliba.pwdmanagerpolibaproject.utils.EncryptionUtils

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val username: String,
    private var encryptedPassword: String,
    val url: String? = null,
    val notes: String? = null
) {
    // Getter for encrypted password
    fun getEncryptedPassword(): String = encryptedPassword

    // Getter for decrypted password
    fun getDecryptedPassword(): String {
        val decrypted = EncryptionUtils.decryptPassword(encryptedPassword)
        // If the decrypted password is still a hash, it means it's the old format
        // In this case, we can't show the original password
        return if (decrypted.length == 64) { // SHA-256 hash length
            "Password stored in old format"
        } else {
            decrypted
        }
    }

    // Setter for password
    fun setPassword(password: String) {
        encryptedPassword = EncryptionUtils.encryptPassword(password)
    }

    companion object {
        fun create(
            title: String,
            username: String,
            password: String,
            url: String? = null,
            notes: String? = null
        ): PasswordEntity {
            return PasswordEntity(
                title = title,
                username = username,
                encryptedPassword = EncryptionUtils.encryptPassword(password),
                url = url,
                notes = notes
            )
        }
    }
} 