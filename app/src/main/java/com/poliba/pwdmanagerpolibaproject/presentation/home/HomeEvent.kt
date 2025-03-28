package com.poliba.pwdmanagerpolibaproject.presentation.home

import android.content.Context
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData

sealed class HomeEvent {

    data object OnAddPwdClick: HomeEvent()
    data object OnBack: HomeEvent()
    data class OnPasswordDataChange(val newPassword: PasswordData): HomeEvent()
    data class OnSavePassword(val newPassword: PasswordData): HomeEvent()
    data class OnDeletePassword(val password: PasswordEntity): HomeEvent()
    data class OnOpenLink(val context: Context, val url: String): HomeEvent()
}