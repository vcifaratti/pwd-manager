package com.poliba.pwdmanagerpolibaproject.presentation.profile

sealed class ProfileEvent {
    data object OnLogoutClick : ProfileEvent()
}