package com.poliba.pwdmanagerpolibaproject.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    var currentUser: FirebaseUser? = null
        get() = auth.currentUser
        private set

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            state = state.copy(
                isAuthenticated = firebaseAuth.currentUser != null
            )
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.OnEmailChange -> {
                state = state.copy(email = event.email)
            }
            is AuthEvent.OnPasswordChange -> {
                state = state.copy(password = event.password)
            }
            is AuthEvent.OnConfirmPasswordChange -> {
                state = state.copy(confirmPassword = event.confirmPassword)
            }
            AuthEvent.OnLoginClick -> {
                login()
            }
            AuthEvent.OnSignupClick -> {
                signup()
            }
            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)
                auth.signInWithEmailAndPassword(state.email, state.password).await()
                state = state.copy(isLoading = false)
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred during login"
                )
            }
        }
    }

    private fun signup() {
        println("test")
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)
                
                // Validate inputs
                if (state.email.isBlank() || state.password.isBlank() ) {
                    state = state.copy(
                        isLoading = false,
                        error = "All fields are required"
                    )
                    return@launch
                }

                if (state.password != state.confirmPassword) {
                    state = state.copy(
                        isLoading = false,
                        error = "Passwords do not match"
                    )
                    return@launch
                }

                if (state.password.length < 6) {
                    state = state.copy(
                        isLoading = false,
                        error = "Password must be at least 6 characters long"
                    )
                    return@launch
                }

                // Create user with email and password
                val result = auth.createUserWithEmailAndPassword(state.email, state.password).await()

                println("User created: ${result.user?.uid}")
                
                // Update user profile with username
                result.user?.updateProfile(
                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(state.email)
                        .build()
                )?.await()

                state = state.copy(
                    isLoading = false,
                    isSignupSuccess = true
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = when {
                        e.message?.contains("email address is already in use") == true -> 
                            "This email is already registered"
                        e.message?.contains("badly formatted") == true -> 
                            "Invalid email format"
                        e.message?.contains("password is invalid") == true -> 
                            "Password must be at least 6 characters long"
                        else -> e.message ?: "An error occurred during signup"
                    }
                )
            }
        }
    }

}