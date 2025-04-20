package com.poliba.pwdmanagerpolibaproject.utils

import android.util.Base64
import javax.crypto.Cipher
import java.security.MessageDigest

object EncryptionUtils {
    private const val ALGORITHM = "AES/CBC/PKCS7Padding"

    fun encryptPassword(password: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val key = KeyManager.getOrCreateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        
        // Combine IV and encrypted data
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decryptPassword(encryptedPassword: String): String {
        return try {
            val combined = Base64.decode(encryptedPassword, Base64.DEFAULT)
            
            // Extract IV and encrypted data
            val iv = ByteArray(16) // AES block size
            val encryptedBytes = ByteArray(combined.size - iv.size)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(ALGORITHM)
            val key = KeyManager.getOrCreateKey()
            cipher.init(Cipher.DECRYPT_MODE, key, javax.crypto.spec.IvParameterSpec(iv))
            String(cipher.doFinal(encryptedBytes))
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