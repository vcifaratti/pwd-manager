package com.poliba.pwdmanagerpolibaproject.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSignupSuccess: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)