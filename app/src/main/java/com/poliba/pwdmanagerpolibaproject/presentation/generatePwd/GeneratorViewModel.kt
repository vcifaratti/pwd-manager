package com.poliba.pwdmanagerpolibaproject.presentation.generatePwd

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GeneratorViewModel @Inject constructor(): ViewModel() {

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
                state.copy(showSaveDialog = true)
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
            is GeneratorEvent.OnSaveClick -> {
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
                state.copy(
                    showSaveDialog = false,
                    passwordData = PasswordData("", "", "", "", "")
                )
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