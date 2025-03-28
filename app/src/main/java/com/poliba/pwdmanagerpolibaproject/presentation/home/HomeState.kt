package com.poliba.pwdmanagerpolibaproject.presentation.home

import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity
import com.poliba.pwdmanagerpolibaproject.utils.PasswordData

data class HomeState (
    val newPasswordData: PasswordData? = PasswordData("","","","",""),
    val passwords: List<PasswordEntity> = emptyList()
)