package com.poliba.pwdmanagerpolibaproject.navigation

import com.poliba.pwdmanagerpolibaproject.R

sealed class BottomNavigationItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavigationItem("HOME", R.drawable.ic_launcher_foreground, "Home")
    object GeneratePassword : BottomNavigationItem("GENERATE_PASSWORD", R.drawable.ic_launcher_foreground, "Generate")
    object Profile : BottomNavigationItem("PROFILE", R.drawable.ic_launcher_foreground, "Profile")

    companion object {
        fun getAll() = listOf(Home, GeneratePassword, Profile)
    }

    }