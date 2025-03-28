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

    //------------------------ handle auth events
    fun handleEvents(event: HomeEvent){
        when (event){
            is HomeEvent.OnPasswordDataChange -> state = state.copy(newPasswordData = event.newPassword)
            is HomeEvent.OnSavePassword -> {
                viewModelScope.launch {
                    event.newPassword.let { passwordData ->
                        dao.insertPassword(
                            PasswordEntity(
                                title = passwordData.title ?: "",
                                username = passwordData.username ?: "",
                                password = passwordData.password ?: "",
                                url = passwordData.url ?: "",
                                notes = passwordData.notes ?: ""
                            )
                        )
                    }
                }
            }
            is HomeEvent.OnDeletePassword -> {
                viewModelScope.launch {
                    dao.deletePassword(event.password)
                }
            }
            is HomeEvent.OnOpenLink -> openUrl(event.context, event.url)
            else -> Unit
        }
    }
}