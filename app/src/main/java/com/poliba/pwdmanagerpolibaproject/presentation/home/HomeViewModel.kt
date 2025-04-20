package com.poliba.pwdmanagerpolibaproject.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordDao
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.data.remote.FirebaseSync
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dao: PasswordDao,
    private val firebaseSync: FirebaseSync,
    private val auth: FirebaseAuth
): ViewModel() {

    private val TAG = "HomeViewModel"
    var state by mutableStateOf(HomeState())
        private set
    
    // Get current user ID
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "not_logged_in"

    init {
        viewModelScope.launch {
            dao.getAllPasswords().collectLatest { passwords ->
                Log.d(TAG, "Passwords received from database: ${passwords.size}")
                passwords.forEachIndexed { index, pwd ->
                    Log.d(TAG, "Password $index: id=${pwd.id}, title=${pwd.title}, username=${pwd.username}")
                }
                state = state.copy(
                    passwords = passwords
                )
            }
        }

        // Observe sync state
        viewModelScope.launch {
            firebaseSync.syncState.collectLatest { syncState ->
                state = state.copy(syncState = syncState)
            }
        }

        // Start Firebase sync with current user ID
        auth.currentUser?.let { user ->
            firebaseSync.syncWithFirebase(user.uid)
        }
    }

    private fun openUrl(context: Context, url: String) {
        try {
            // Format URL if it doesn't start with http:// or https://
            val formattedUrl = when {
                url.startsWith("http://") || url.startsWith("https://") -> url
                url.startsWith("www.") -> "https://$url"
                else -> "https://www.$url"
            }

            Log.d(TAG, "Opening URL: $formattedUrl")
            
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening URL: ${e.message}")
        }
    }

    fun handleEvents(event: HomeEvent) {
        when (event) {
            HomeEvent.OnAddPwdClick -> {
                state = state.copy(
                    showAddPasswordDialog = true,
                    newPasswordData = PasswordData()
                )
            }
            HomeEvent.OnBack -> {
                state = state.copy(
                    showAddPasswordDialog = false,
                    newPasswordData = null
                )
            }
            is HomeEvent.OnPasswordDataChange -> {
                state = state.copy(newPasswordData = event.passwordData)
            }
            is HomeEvent.OnSavePassword -> {
                if (event.passwordData.isValid()) {
                    viewModelScope.launch {
                        val passwordEntity = PasswordEntity.create(
                            title = event.passwordData.title,
                            username = event.passwordData.username,
                            password = event.passwordData.password,
                            url = event.passwordData.url,
                            notes = event.passwordData.notes
                        )
                        
                        Log.d(TAG, "Saving new password: ${passwordEntity.title}, with ID before insert: ${passwordEntity.id}")
                        
                        val insertedId = dao.insertPassword(passwordEntity)
                        
                        Log.d(TAG, "Insert result ID: $insertedId")
                        
                        // If the insertion was successful
                        if (insertedId > 0) {
                            // Need to get the newly assigned ID
                            passwordEntity.id = insertedId.toInt()
                            Log.d(TAG, "Password saved with ID: ${passwordEntity.id}")
                            
                            // Upload to Firebase with user ID
                            firebaseSync.uploadToFirebase(currentUserId, passwordEntity)
                            
                            state = state.copy(
                                newPasswordData = null,
                                showAddPasswordDialog = false
                            )
                        } else {
                            // Handle case where insertion failed (likely due to conflict)
                            Log.e(TAG, "Failed to insert password, possible conflict")
                            // You might want to add feedback to the user here
                        }
                    }
                }
            }
            is HomeEvent.OnDeletePassword -> {
                state = state.copy(
                    passwordToDelete = event.password,
                    showDeleteConfirmation = true
                )
            }
            HomeEvent.OnShowDeleteConfirmation -> {
                state = state.copy(showDeleteConfirmation = true)
            }
            HomeEvent.OnHideDeleteConfirmation -> {
                state = state.copy(
                    showDeleteConfirmation = false,
                    passwordToDelete = null
                )
            }
            HomeEvent.OnConfirmDelete -> {
                state.passwordToDelete?.let { password ->
                    viewModelScope.launch {
                        dao.deletePassword(password)
                        // Delete from Firebase with user ID
                        firebaseSync.deleteFromFirebase(currentUserId, password.id)
                        state = state.copy(
                            showDeleteConfirmation = false,
                            passwordToDelete = null
                        )
                    }
                }
            }
            is HomeEvent.OnUpdatePassword -> {
                viewModelScope.launch {
                    val updatedPassword = event.password.copy(
                        title = event.newTitle,
                        username = event.newUsername,
                        url = event.newUrl,
                        notes = event.newNotes
                    )
                    updatedPassword.setPassword(event.newPassword)
                    dao.updatePassword(updatedPassword)
                    // Update in Firebase with user ID
                    firebaseSync.uploadToFirebase(currentUserId, updatedPassword)
                }
            }
            is HomeEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                viewModelScope.launch {
                    dao.searchPasswords(event.query).collectLatest { passwords ->
                        state = state.copy(passwords = passwords)
                    }
                }
            }
            is HomeEvent.OnOpenLink -> {
                if (event.url.isNotBlank()) {
                    openUrl(event.context, event.url)
                }
            }
            is HomeEvent.OnViewPassword -> {
                state = state.copy(viewingPassword = event.password)
            }
            HomeEvent.OnHidePassword -> {
                state = state.copy(viewingPassword = null)
            }
        }
    }
}