package com.poliba.pwdmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poliba.pwdmanagerpolibaproject.presentation.home.HomeEvent
import com.poliba.pwdmanagerpolibaproject.presentation.home.HomeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Password") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(HomeEvent.OnBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            state.newPasswordData?.title?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { newTitle ->
                        val updatedPasswordData = state.newPasswordData.copy(title = newTitle)
                        onEvent(HomeEvent.OnPasswordDataChange(updatedPasswordData))
                    },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Username field
            state.newPasswordData?.username?.let { username ->
                OutlinedTextField(
                    value = username,
                    onValueChange = { newData ->
                        val updatedData = state.newPasswordData.copy(username = newData)
                        onEvent(HomeEvent.OnPasswordDataChange(updatedData))
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Password field with visibility toggle
            state.newPasswordData?.password?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { newData ->
                        val updatedData = state.newPasswordData.copy(password = newData)
                        onEvent(HomeEvent.OnPasswordDataChange(updatedData))
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.Close else Icons.Default.Face,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Website field
            state.newPasswordData?.url?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { newData ->
                        val updatedData = state.newPasswordData.copy(url = newData)
                        onEvent(HomeEvent.OnPasswordDataChange(updatedData))
                    },
                    label = { Text("Website (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Notes field
            state.newPasswordData?.notes?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { newData ->
                        val updatedData = state.newPasswordData.copy(notes = newData)
                        onEvent(HomeEvent.OnPasswordDataChange(updatedData))
                    },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }

            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Save button
            Button(
                onClick = {
                    when {
                        state.newPasswordData?.title?.isBlank() == true -> {
                            showError = true
                            errorMessage = "Title is required"
                        }
                        state.newPasswordData?.username?.isBlank() == true -> {
                            showError = true
                            errorMessage = "Username is required"
                        }
                        state.newPasswordData?.password?.isBlank() == true -> {
                            showError = true
                            errorMessage = "Password is required"
                        }
                        else -> {
                            // TODO: Save password to database
                            state.newPasswordData?.let { HomeEvent.OnSavePassword(it) }
                                ?.let { onEvent(it) }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Password")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddPasswordScreenPreview() {
    AddPasswordScreen(
        state = HomeState(),
        onEvent = {}
    )
}