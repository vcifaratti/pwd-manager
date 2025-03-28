package com.poliba.pwdmanagerpolibaproject.presentation.generatePwd

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    state: GeneratorState,
    onEvent: (GeneratorEvent) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showCopiedMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Password Generator",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Password Length Slider
        Text(
            text = "Password Length: ${state.passwordLength}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = state.passwordLength.toFloat(),
            onValueChange = {
                onEvent(GeneratorEvent.OnPasswordLengthChange(it.toInt()))
            },
            valueRange = 8f..32f,
            steps = 23,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Character Type Checkboxes
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = state.includeUppercase,
                    onCheckedChange = {
                        onEvent(GeneratorEvent.OnUppercaseChange(it))
                    }
                )
                Text(
                    text = "Uppercase Letters (A-Z)",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = state.includeLowercase,
                    onCheckedChange = {
                        onEvent(GeneratorEvent.OnLowercaseChange(it))
                    }
                )
                Text(
                    text = "Lowercase Letters (a-z)",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = state.includeNumbers,
                    onCheckedChange = {
                        onEvent(GeneratorEvent.OnNumbersChange(it))
                    }
                )
                Text(
                    text = "Numbers (0-9)",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = state.includeSpecialChars,
                    onCheckedChange = {
                        onEvent(GeneratorEvent.OnSpecialCharsChange(it))
                    }
                )
                Text(
                    text = "Special Characters (!@#$%^&*)",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Generated Password Display
        if (state.generatedPassword.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.generatedPassword,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(state.generatedPassword))
                            showCopiedMessage = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Copy to clipboard"
                        )
                    }
                }
            }
        }

        if (showCopiedMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showCopiedMessage = false
            }
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Password copied to clipboard")
            }
        }

        // Generate Button
        Button(
            onClick = { onEvent(GeneratorEvent.OnGeneratePassword) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Generate New Password")
        }

        // Save Button (only show if there's a generated password)
        if (state.generatedPassword.isNotEmpty()) {
            Button(
                onClick = { onEvent(GeneratorEvent.OnSaveClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Save Password")
            }
        }
    }

    // Save Dialog
    if (state.showSaveDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(GeneratorEvent.OnCancelSave) },
            title = { Text("Save Password") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.passwordData.title ?: "",
                        onValueChange = { onEvent(GeneratorEvent.OnPasswordDataChange("title", it)) },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.passwordData.username ?: "",
                        onValueChange = { onEvent(GeneratorEvent.OnPasswordDataChange("username", it)) },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.passwordData.url ?: "",
                        onValueChange = { onEvent(GeneratorEvent.OnPasswordDataChange("url", it)) },
                        label = { Text("URL (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.passwordData.notes ?: "",
                        onValueChange = { onEvent(GeneratorEvent.OnPasswordDataChange("notes", it)) },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onEvent(GeneratorEvent.OnSavePassword) }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onEvent(GeneratorEvent.OnCancelSave) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneratorScreenPreview() {
    GeneratorScreen(
        state = GeneratorState(),
        onEvent = {}
    )
}