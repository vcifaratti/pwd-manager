package com.poliba.pwdmanagerpolibaproject.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        // Get current user information
        auth.currentUser?.let { user ->
            state = state.copy(
                userEmail = user.email ?: "",
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.OnLogoutClick -> {
                auth.signOut()
            }
        }
    }
}