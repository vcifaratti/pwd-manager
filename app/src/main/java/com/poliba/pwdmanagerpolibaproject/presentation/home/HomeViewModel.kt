package com.poliba.pwdmanagerpolibaproject.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordDao
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dao: PasswordDao
): ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        viewModelScope.launch {
            dao.getAllPasswords().collectLatest { passwords ->
                state = state.copy(
                    passwords = passwords
                )
            }
        }
    }

    private fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun handleEvents(event: HomeEvent) {
        when (event) {
            HomeEvent.OnAddPwdClick -> {
                state = state.copy(
                    showAddPasswordDialog = true
                )
            }
            HomeEvent.OnBack -> {
                state = state.copy(
                    showAddPasswordDialog = false,
                    newPasswordData = PasswordData("", "", "", "", "")
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
                        dao.insertPassword(passwordEntity)
                        state = state.copy(
                            newPasswordData = null,
                            showAddPasswordDialog = false
                        )
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
            is HomeEvent.OnOpenLink -> openUrl(event.context, event.url)
            is HomeEvent.OnViewPassword -> {
                state = state.copy(viewingPassword = event.password)
            }
            HomeEvent.OnHidePassword -> {
                state = state.copy(viewingPassword = null)
            }
        }
    }
}