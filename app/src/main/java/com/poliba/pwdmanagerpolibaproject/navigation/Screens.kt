package com.replyconnect.smegconnect.navigation

enum class Screen(
    val route: String = "",
    val type: String = ""
) {
    WelcomeScreen(route = "WELCOME_SCREEN"),
    LoginScreen(route = "LOGIN_SCREEN"),
    SignupScreen(route = "SIGNUP_SCREEN"),
    AddNewPwdScreen(route = "ADD_NEW_PWD_SCREEN"),
}

fun String.getScreenForRoute(): Screen? {
    return Screen.entries.firstOrNull { it.route == this }
}