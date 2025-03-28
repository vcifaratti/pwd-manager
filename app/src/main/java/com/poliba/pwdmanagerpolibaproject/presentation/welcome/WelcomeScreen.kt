package com.poliba.pwdmanagerpolibaproject.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.poliba.pwdmanagerpolibaproject.presentation.welcome.WelcomeEvent

@Composable
fun WelcomeScreen (
    state: WelcomeState,
    onEvent: (WelcomeEvent) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Password Manager",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onEvent(WelcomeEvent.OnViewPwdClick) }
        ) {
            Text("View Passwords")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onEvent(WelcomeEvent.OnGeneratePwdClick) }
        ) {
            Text("Generate Password")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        state = WelcomeState(),
        onEvent = {}
    )
} 