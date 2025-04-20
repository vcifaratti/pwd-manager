package com.poliba.pwdmanagerpolibaproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.poliba.pwdmanagerpolibaproject.utils.EncryptionUtils

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var username: String = "",
    private var encryptedPassword: String = "",
    var url: String? = null,
    var notes: String? = null
) {
    // Required empty constructor for Firebase
    constructor() : this(0, "", "", "", null, null)

    // Getter for encrypted password
    fun getEncryptedPassword(): String = encryptedPassword

    // Getter for decrypted password
    fun getDecryptedPassword(): String {
        return try {
            val decrypted = EncryptionUtils.decryptPassword(encryptedPassword)
            // Check if the decrypted password is still in the old format (SHA-256)
            if (decrypted.length == 64) {
                "Password stored in old format"
            } else {
                decrypted
            }
        } catch (e: Exception) {
            "Error decrypting password"
        }
    }

    // Setter for password
    fun setPassword(password: String) {
        encryptedPassword = EncryptionUtils.encryptPassword(password)
    }

    // Setter for encrypted password (for Firebase synchronization)
    fun setEncryptedPassword(encrypted: String) {
        encryptedPassword = encrypted
    }

    companion object {
        fun create(
            title: String,
            username: String,
            password: String,
            url: String? = null,
            notes: String? = null
        ): PasswordEntity {
            val entity = PasswordEntity(
                title = title,
                username = username,
                url = url,
                notes = notes
            )
            entity.setPassword(password)
            return entity
        }
    }
} 