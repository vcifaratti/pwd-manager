package com.poliba.pwdmanagerpolibaproject.navigation

import com.poliba.pwdmanagerpolibaproject.R

sealed class BottomNavigationItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavigationItem("HOME", R.drawable.icon_home, "Home")
    object GeneratePassword : BottomNavigationItem("GENERATE_PASSWORD", R.drawable.icon_new_pwd, "Generate")
    object Profile : BottomNavigationItem("PROFILE", R.drawable.icon_user, "Profile")

    companion object {
        fun getAll() = listOf(Home, GeneratePassword, Profile)
    }

    }