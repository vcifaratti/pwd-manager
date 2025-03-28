package com.poliba.pwdmanagerpolibaproject.presentation.welcome

sealed class WelcomeEvent {

    data object OnViewPwdClick: WelcomeEvent()

    data object OnGeneratePwdClick: WelcomeEvent()
}