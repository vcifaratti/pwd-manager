package com.poliba.pwdmanagerpolibaproject.data.remote

import android.util.Log
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
    private val TAG = "FirebaseSync"
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentSyncListener: ValueEventListener? = null
    private var currentUserRef: String? = null

    fun syncWithFirebase(userId: String) {
        if (userId.isBlank() || userId == "not_logged_in") {
            Log.e(TAG, "Cannot sync with invalid user ID: $userId")
            _syncState.value = SyncState.Error("Invalid user ID")
            return
        }
        
        _syncState.value = SyncState.Syncing
        Log.d(TAG, "Starting sync for user: $userId")

        // If we're already listening to a different user, remove the listener
        if (currentUserRef != null && currentUserRef != userId && currentSyncListener != null) {
            val prevRef = database.getReference("users/$currentUserRef/passwords")
            prevRef.removeEventListener(currentSyncListener!!)
            currentSyncListener = null
        }

        // Set the current user reference
        currentUserRef = userId
        
        val passwordsRef = database.getReference("users/$userId/passwords")
        
        // Create new listener
        currentSyncListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Firebase data changed: ${snapshot.childrenCount} passwords")
                val firebasePasswords = snapshot.children.mapNotNull { it.getValue(PasswordEntity::class.java) }
                
                // Launch coroutine to update local database
                coroutineScope.launch {
                    updateLocalDatabase(firebasePasswords)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase error: ${error.message}")
                _syncState.value = SyncState.Error(error.message)
            }
        }
        
        // Listen for changes in Firebase
        passwordsRef.addValueEventListener(currentSyncListener!!)
    }

    private suspend fun updateLocalDatabase(firebasePasswords: List<PasswordEntity>) {
        try {
            // Get local passwords
            val localPasswords = passwordDao.getAllPasswordsSync()
            
            Log.d(TAG, "Sync: firebase passwords = ${firebasePasswords.size}, local passwords = ${localPasswords.size}")
            firebasePasswords.forEachIndexed { index, pwd ->
                Log.d(TAG, "Firebase password $index: id=${pwd.id}, title=${pwd.title}")
            }
            localPasswords.forEachIndexed { index, pwd ->
                Log.d(TAG, "Local password $index: id=${pwd.id}, title=${pwd.title}")
            }
            
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
            
            Log.d(TAG, "Sync: adding ${passwordsToAdd.size}, updating ${passwordsToUpdate.size}, deleting ${passwordsToDelete.size}")
            
            // Perform database operations
            passwordDao.insertPasswords(passwordsToAdd)
            passwordDao.updatePasswords(passwordsToUpdate)
            passwordDao.deletePasswords(passwordsToDelete)
            
            _syncState.value = SyncState.Success
        } catch (e: Exception) {
            Log.e(TAG, "Error updating local database: ${e.message}")
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
        }
    }

    fun uploadToFirebase(userId: String, password: PasswordEntity) {
        if (userId.isBlank() || userId == "not_logged_in") {
            Log.e(TAG, "Cannot upload with invalid user ID: $userId")
            _syncState.value = SyncState.Error("Invalid user ID")
            return
        }
        
        // Make sure the password has a valid ID
        if (password.id <= 0) {
            Log.e(TAG, "Cannot upload password with invalid ID: ${password.id}")
            _syncState.value = SyncState.Error("Invalid password ID")
            return
        }
        
        Log.d(TAG, "Uploading password ${password.id} for user $userId")
        val passwordsRef = database.getReference("users/$userId/passwords/${password.id}")
        passwordsRef.setValue(password)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded password to Firebase")
                _syncState.value = SyncState.Success
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload password: ${e.message}")
                _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            }
    }

    fun deleteFromFirebase(userId: String, passwordId: Int) {
        if (userId.isBlank() || userId == "not_logged_in") {
            Log.e(TAG, "Cannot delete with invalid user ID: $userId")
            _syncState.value = SyncState.Error("Invalid user ID")
            return
        }
        
        Log.d(TAG, "Deleting password $passwordId for user $userId")
        val passwordRef = database.getReference("users/$userId/passwords/$passwordId")
        passwordRef.removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Successfully deleted password from Firebase")
                _syncState.value = SyncState.Success
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete password: ${e.message}")
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