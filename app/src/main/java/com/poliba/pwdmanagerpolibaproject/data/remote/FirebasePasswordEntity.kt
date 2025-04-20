package com.poliba.pwdmanagerpolibaproject.data.remote

import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity

/**
 * Secure version of the PasswordEntity entity for Firebase.
 * This class does not contain the decryption functions
 * and does not include methods that would expose the password in plain text.
 */
data class FirebasePasswordEntity(
    var id: Int = 0,
    var title: String = "",
    var username: String = "",
    var encryptedPassword: String = "", // Questo campo conterrà solo la versione criptata
    var url: String? = null,
    var notes: String? = null
) {
    constructor() : this(0, "", "", "", null, null)
    
    companion object {
        /**
         * Converts a PasswordEntity to FirebasePasswordEntity
         * to ensure that only the encrypted version of the password
         * is saved on Firebase
         */
        fun fromPasswordEntity(entity: PasswordEntity): FirebasePasswordEntity {
            return FirebasePasswordEntity(
                id = entity.id,
                title = entity.title,
                username = entity.username,
                encryptedPassword = entity.getEncryptedPassword(),
                url = entity.url,
                notes = entity.notes
            )
        }

        /**
         * Converts a FirebasePasswordEntity to PasswordEntity
         * for local use in the app
         */
        fun toPasswordEntity(firebaseEntity: FirebasePasswordEntity): PasswordEntity {
            val entity = PasswordEntity(
                id = firebaseEntity.id,
                title = firebaseEntity.title,
                username = firebaseEntity.username,
                url = firebaseEntity.url,
                notes = firebaseEntity.notes
            )
            entity.setEncryptedPassword(firebaseEntity.encryptedPassword)
            return entity
        }
    }
} 