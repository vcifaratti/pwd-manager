package com.poliba.pwdmanagerpolibaproject.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordDao
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSync @Inject constructor(
    private val database: FirebaseDatabase,
    private val passwordDao: PasswordDao
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun syncWithFirebase(userId: String) {
        _syncState.value = SyncState.Syncing

        val passwordsRef = database.getReference("users/$userId/passwords")
        
        // Listen for changes in Firebase
        passwordsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebasePasswords = snapshot.children.mapNotNull { it.getValue(PasswordEntity::class.java) }
                
                // Launch coroutine to update local database
                coroutineScope.launch {
                    updateLocalDatabase(firebasePasswords)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _syncState.value = SyncState.Error(error.message)
            }
        })
    }

    private suspend fun updateLocalDatabase(firebasePasswords: List<PasswordEntity>) {
        try {
            // Get local passwords
            val localPasswords = passwordDao.getAllPasswordsSync()
            
            // Find passwords to add or update
            val passwordsToAdd = firebasePasswords.filter { firebasePwd ->
                localPasswords.none { it.id == firebasePwd.id }
            }
            
            val passwordsToUpdate = firebasePasswords.filter { firebasePwd ->
                localPasswords.any { it.id == firebasePwd.id && it != firebasePwd }
            }
            
            // Find passwords to delete
            val passwordsToDelete = localPasswords.filter { localPwd ->
                firebasePasswords.none { it.id == localPwd.id }
            }
            
            // Perform database operations
            passwordDao.insertPasswords(passwordsToAdd)
            passwordDao.updatePasswords(passwordsToUpdate)
            passwordDao.deletePasswords(passwordsToDelete)
            
            _syncState.value = SyncState.Success
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
        }
    }

    fun uploadToFirebase(userId: String, password: PasswordEntity) {
        val passwordsRef = database.getReference("users/$userId/passwords/${password.id}")
        passwordsRef.setValue(password)
            .addOnSuccessListener {
                _syncState.value = SyncState.Success
            }
            .addOnFailureListener { e ->
                _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            }
    }

    fun deleteFromFirebase(userId: String, passwordId: Int) {
        val passwordRef = database.getReference("users/$userId/passwords/$passwordId")
        passwordRef.removeValue()
            .addOnSuccessListener {
                _syncState.value = SyncState.Success
            }
            .addOnFailureListener { e ->
                _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            }
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
} 