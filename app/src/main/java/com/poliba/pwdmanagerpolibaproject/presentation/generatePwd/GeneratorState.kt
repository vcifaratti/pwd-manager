package com.poliba.pwdmanagerpolibaproject.presentation.generatePwd

import com.poliba.pwdmanagerpolibaproject.utils.PasswordData

data class GeneratorState(
    val passwordLength: Int = 12,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSpecialChars: Boolean = true,
    val generatedPassword: String = "",
    val showSaveDialog: Boolean = false,
    val passwordData: PasswordData = PasswordData("", "", "", "", "")
)