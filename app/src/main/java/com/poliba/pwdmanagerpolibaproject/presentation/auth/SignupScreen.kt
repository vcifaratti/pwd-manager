package com.poliba.pwdmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poliba.pwdmanagerpolibaproject.presentation.auth.AuthEvent
import com.poliba.pwdmanagerpolibaproject.presentation.auth.AuthState

@Composable
fun SignupScreen(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (state.isSignupSuccess) {
            // Show success message
            onEvent(AuthEvent.OnSignupSuccess)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(AuthEvent.OnEmailChange(it)) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(AuthEvent.OnPasswordChange(it)) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { onEvent(AuthEvent.OnConfirmPasswordChange(it)) },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                when {
                    state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank() -> {
                        showError = true
                        errorMessage = "All fields are required"
                    }
                    state.password != state.confirmPassword -> {
                        showError = true
                        errorMessage = "Passwords do not match"
                    }
                    !state.email.contains("@") -> {
                        showError = true
                        errorMessage = "Invalid email format"
                    }
                    else -> {
                        // Mock successful signup
                        onEvent(AuthEvent.OnSignupClick)
                        showError = false
                        errorMessage = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Sign Up")
        }

        TextButton(
            onClick = { onEvent(AuthEvent.OnNavigateToLogin) }
        ) {
            Text("Already have an account? Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        state = AuthState(),
        onEvent = {}
    )
}