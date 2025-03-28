package com.poliba.pwdmanagerpolibaproject.presentation.home

import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData

data class HomeState(
    val newPasswordData: PasswordData? = PasswordData("", "", "", "", ""),
    val passwords: List<PasswordEntity> = emptyList(),
    val searchQuery: String = "",
    val showAddPasswordDialog: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val passwordToDelete: PasswordEntity? = null,
    val viewingPassword: PasswordEntity? = null
)