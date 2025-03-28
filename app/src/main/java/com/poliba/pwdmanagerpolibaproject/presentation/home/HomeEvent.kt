package com.poliba.pwdmanagerpolibaproject.presentation.home

import android.content.Context
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData

sealed class HomeEvent {

    data object OnAddPwdClick: HomeEvent()
    data object OnBack: HomeEvent()
    data class OnPasswordDataChange(val passwordData: PasswordData): HomeEvent()
    data class OnSavePassword(val passwordData: PasswordData): HomeEvent()
    data class OnDeletePassword(val password: PasswordEntity): HomeEvent()
    data class OnOpenLink(val context: Context, val url: String): HomeEvent()
    data class OnSearchQueryChange(val query: String): HomeEvent()
    data class OnUpdatePassword(
        val password: PasswordEntity,
        val newTitle: String,
        val newUsername: String,
        val newPassword: String,
        val newUrl: String,
        val newNotes: String
    ): HomeEvent()
    data object OnShowDeleteConfirmation: HomeEvent()
    data object OnHideDeleteConfirmation: HomeEvent()
    data object OnConfirmDelete: HomeEvent()
    data class OnViewPassword(val password: PasswordEntity): HomeEvent()
    data object OnHidePassword: HomeEvent()
}