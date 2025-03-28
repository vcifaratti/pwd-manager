package com.poliba.pwdmanagerpolibaproject.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

object EncryptionUtils {
    private const val ALGORITHM = "AES"
    private const val KEY = "YourSecretKey123" // In a real app, this should be stored securely

    fun encryptPassword(password: String): String {
        val key = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decryptPassword(encryptedPassword: String): String {
        return try {
            // Try to decrypt as AES
            val key = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT))
            String(decryptedBytes)
        } catch (e: Exception) {
            // If decryption fails, it's probably the old SHA-256 format
            // Return the hash as is
            encryptedPassword
        }
    }

    // Legacy method for backward compatibility
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
} 