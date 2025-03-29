package com.poliba.pwdmanagerpolibaproject.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.poliba.pwdmanagerpolibaproject.data.local.PasswordEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Passwords") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(HomeEvent.OnAddPwdClick) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Password")
            }
        }
    ) { padding ->
        if (state.passwords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No passwords saved yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.passwords) { password ->
                    PasswordCard(
                        password = password,
                        onDeleteClick = {
                            onEvent(HomeEvent.OnDeletePassword(password))
                        },
                        onUrlClick = {
                            if (password.url?.isNotBlank() == true) {
                                try {
                                    onEvent(HomeEvent.OnOpenLink(context, password.url!!))
                                } catch (e: Exception) {
                                    // Handle invalid URL or other errors
                                }
                            }
                        },
                        onCardClick = {
                            onEvent(HomeEvent.OnViewPassword(password))
                        }
                    )
                }
            }
        }

        // Password Viewing Dialog
        state.viewingPassword?.let { password ->
            AlertDialog(
                onDismissRequest = { onEvent(HomeEvent.OnHidePassword) },
                title = { Text("Password Details") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Title: ${password.title}")
                        Text("Username: ${password.username}")
                        Text("Password: ${password.getDecryptedPassword()}")
                        password.url?.let { Text("URL: $it") }
                        password.notes?.let { Text("Notes: $it") }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onEvent(HomeEvent.OnHidePassword) }) {
                        Text("Close")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (state.showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(HomeEvent.OnHideDeleteConfirmation) },
                title = { Text("Delete Password") },
                text = { Text("Are you sure you want to delete this password?") },
                confirmButton = {
                    TextButton(
                        onClick = { onEvent(HomeEvent.OnConfirmDelete) }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onEvent(HomeEvent.OnHideDeleteConfirmation) }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PasswordCard(
    password: PasswordEntity,
    onDeleteClick: () -> Unit,
    onUrlClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val hasUrl = password.url?.isNotBlank()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = password.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Password",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Username: ${password.username}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Password: •••••••••••••••",
                style = MaterialTheme.typography.bodyMedium
            )
            if (hasUrl == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUrlClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Open URL",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = password.url ?: "url not found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (password.notes?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${password.notes}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        state = HomeState(),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PwdCardPreview() {
    PasswordCard(
        password = PasswordEntity(
            id = 0,
            title = "Prova",
            username = "prova",
            encryptedPassword = "prova",
            url = "https://www.prova.com",
            notes = "prova"
        ),
        onDeleteClick = {},
        onUrlClick = {},
        onCardClick = {}
    )
}