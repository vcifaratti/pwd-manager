package com.poliba.pwdmanagerpolibaproject.presentation.auth

sealed class AuthEvent {
    data class OnEmailChange(val email: String) : AuthEvent()
    data class OnPasswordChange(val password: String) : AuthEvent()
    data class OnUsernameChange(val username: String) : AuthEvent()
    data class OnConfirmPasswordChange(val confirmPassword: String) : AuthEvent()
    data object OnLoginClick : AuthEvent()
    data object OnSignupClick : AuthEvent()
    data object OnAuthSuccess : AuthEvent()
    data object OnNavigateToSignup : AuthEvent()
    data object OnNavigateToLogin : AuthEvent()
    data object OnSignupSuccess : AuthEvent()
    data object OnLoginSuccess : AuthEvent()
}