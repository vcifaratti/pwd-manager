package com.poliba.pwdmanagerpolibaproject.utils

data class PasswordData(
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val url: String = "",
    val notes: String = ""
) {
    fun isValid(): Boolean {
        return title.isNotBlank() && username.isNotBlank() && password.isNotBlank()
    }
}