package com.poliba.pwdmanagerpolibaproject.presentation.profile

sealed class ProfileEvent {
    object OnLogoutClick : ProfileEvent()
}