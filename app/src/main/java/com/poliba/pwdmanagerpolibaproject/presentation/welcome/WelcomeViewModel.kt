package com.poliba.pwdmanagerpolibaproject.presentation.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.poliba.pwdmanagerpolibaproject.presentation.home.WelcomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(): ViewModel(){

    var state by mutableStateOf(WelcomeState())
        private set

}