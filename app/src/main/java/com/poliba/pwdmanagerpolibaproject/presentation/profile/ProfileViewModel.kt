package com.poliba.pwdmanagerpolibaproject.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.poliba.pwdmanagerpolibaproject.presentation.auth.AuthEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    var currentUser: FirebaseUser? = null
        get() = auth.currentUser
        private set

    init {

    }

    fun onEvent(event: ProfileEvent) {
        when (event) {

            ProfileEvent.OnLogoutClick -> {
                logout()
            }

            else -> Unit
        }
    }


    private fun logout() {
        auth.signOut()
    }
}