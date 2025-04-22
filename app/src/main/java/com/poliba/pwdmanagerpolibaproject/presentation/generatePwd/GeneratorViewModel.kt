package com.poliba.pwdmanagerpolibaproject.presentation.generatePwd

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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val dao: PasswordDao,
    private val firebaseSync: FirebaseSync,
    private val auth: FirebaseAuth
): ViewModel() {

    var state by mutableStateOf(GeneratorState())
        private set

    init {
        // Generate an initial password when the ViewModel is created
        generatePassword()
    }

    fun handleEvent(event: GeneratorEvent) {
        state = when (event) {
            is GeneratorEvent.OnGeneratePassword -> {
                generatePassword()
                state.copy(showSaveDialog = false)
            }
            is GeneratorEvent.OnPasswordLengthChange -> {
                state.copy(passwordLength = event.length.coerceIn(8, 32))
            }
            is GeneratorEvent.OnUppercaseChange -> {
                state.copy(includeUppercase = event.include)
            }
            is GeneratorEvent.OnLowercaseChange -> {
                state.copy(includeLowercase = event.include)
            }
            is GeneratorEvent.OnNumbersChange -> {
                state.copy(includeNumbers = event.include)
            }
            is GeneratorEvent.OnSpecialCharsChange -> {
                state.copy(includeSpecialChars = event.include)
            }
            is GeneratorEvent.OnCopyPassword -> {
                state.copy(showSaveDialog = true)
            }
            is GeneratorEvent.OnCancelSave -> {
                state.copy(showSaveDialog = false)
            }
            is GeneratorEvent.OnPasswordDataChange -> {
                state.copy(
                    passwordData = when (event.field) {
                        "title" -> state.passwordData.copy(title = event.value)
                        "username" -> state.passwordData.copy(username = event.value)
                        "url" -> state.passwordData.copy(url = event.value)
                        "notes" -> state.passwordData.copy(notes = event.value)
                        else -> state.passwordData
                    }
                )
            }
            is GeneratorEvent.OnSavePassword -> {
                if (event.passwordData.isValid()) {
                    savePasswordToDatabase(event.passwordData)
                    state.copy(showSaveDialog = false)
                } else {
                    state.copy(showSaveDialog = true)
                }
            }
        }
    }

    private fun savePasswordToDatabase(passwordData: PasswordData) {
        viewModelScope.launch {
            try {
                val passwordEntity = PasswordEntity.create(
                    title = passwordData.title,
                    username = passwordData.username,
                    password = state.generatedPassword,
                    url = passwordData.url,
                    notes = passwordData.notes
                )
                
                // Ottieni l'ID assegnato dal database locale
                val insertedId = dao.insertPassword(passwordEntity)
                
                // Se l'inserimento è riuscito
                if (insertedId > 0) {
                    // Aggiorna l'ID dell'entità
                    passwordEntity.id = insertedId.toInt()
                    
                    // Carica la password in Firebase con l'ID corretto
                    val userId = auth.currentUser?.uid ?: ""
                    if (userId.isNotBlank()) {
                        firebaseSync.uploadToFirebase(userId, passwordEntity)
                    }
                }
            } catch (e: Exception) {
                // Gestisci eventuali errori
                Log.e("GeneratorViewModel", "Error saving password: ${e.message}")
            }
        }
    }

    private fun generatePassword() {
        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        var allowedChars = ""
        
        if (state.includeUppercase) allowedChars += uppercaseChars
        if (state.includeLowercase) allowedChars += lowercaseChars
        if (state.includeNumbers) allowedChars += numberChars
        if (state.includeSpecialChars) allowedChars += specialChars

        // If no character types are selected, default to lowercase
        if (allowedChars.isEmpty()) {
            allowedChars = lowercaseChars
            state = state.copy(includeLowercase = true)
        }

        val password = buildString {
            repeat(state.passwordLength) {
                append(allowedChars[Random.nextInt(allowedChars.length)])
            }
        }

        state = state.copy(
            generatedPassword = password,
            passwordData = state.passwordData.copy(password = password)
        )
    }
}