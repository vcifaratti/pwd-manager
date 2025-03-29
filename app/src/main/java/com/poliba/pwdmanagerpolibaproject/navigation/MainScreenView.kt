package com.poliba.pwdmanagerpolibaproject.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.replyconnect.smegconnect.navigation.Graph
import com.replyconnect.smegconnect.navigation.HomeNavGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBarState = rememberSaveable { (mutableStateOf(true)) }
    showBottomBarState.value = showBottomBar(navBackStackEntry?.destination?.route)

    Scaffold(
        bottomBar = {
            if (showBottomBarState.value) {
                BottomNavigationBar(
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeNavGraph(
                navController = navController
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 5.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavigationItem.getAll()
            .forEach { screen ->
                val selected = currentRoute == screen.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = screen.icon),
                            contentDescription = screen.title,
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                                .size(24.dp),
                            tint = if (selected) 
                                MaterialTheme.colorScheme.secondary 
                            else 
                                MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    label = {
                        Text(
                            screen.title,
                            textAlign = TextAlign.Center,
                            color = if (selected) 
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else 
                                MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
    }
}

private fun showBottomBar(screen: String?): Boolean {
    return when (screen) {
        Graph.WELCOME -> false
        else -> true
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreenView()
}