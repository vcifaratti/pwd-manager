package com.poliba.pwdmanagerpolibaproject.data.remote

import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity

/**
 * Versione sicura dell'entità PasswordEntity per Firebase.
 * Questa classe non contiene le funzioni di decriptazione 
 * e non include metodi che potrebbero esporre la password in chiaro.
 */
data class FirebasePasswordEntity(
    var id: Int = 0,
    var title: String = "",
    var username: String = "",
    var encryptedPassword: String = "", // Questo campo conterrà solo la versione criptata
    var url: String? = null,
    var notes: String? = null
) {
    // Costruttore vuoto richiesto da Firebase
    constructor() : this(0, "", "", "", null, null)
    
    companion object {
        /**
         * Converte una PasswordEntity in FirebasePasswordEntity
         * per garantire che solo la versione criptata della password 
         * venga salvata su Firebase
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
         * Converte una FirebasePasswordEntity in PasswordEntity
         * per l'uso locale nell'app
         */
        fun toPasswordEntity(firebaseEntity: FirebasePasswordEntity): PasswordEntity {
            val entity = PasswordEntity(
                id = firebaseEntity.id,
                title = firebaseEntity.title,
                username = firebaseEntity.username,
                url = firebaseEntity.url,
                notes = firebaseEntity.notes
            )
            // Imposta la password criptata direttamente
            entity.setEncryptedPassword(firebaseEntity.encryptedPassword)
            return entity
        }
    }
} 