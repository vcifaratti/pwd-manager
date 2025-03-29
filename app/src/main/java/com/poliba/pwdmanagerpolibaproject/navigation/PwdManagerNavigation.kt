package com.replyconnect.smegconnect.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.poliba.pwdmanager.screens.AddPasswordScreen
import com.poliba.pwdmanagerpolibaproject.presentation.generatePwd.GeneratorScreen
import com.poliba.pwdmanagerpolibaproject.navigation.BottomNavigationItem
import com.poliba.pwdmanagerpolibaproject.navigation.MainScreenView
import com.poliba.pwdmanagerpolibaproject.presentation.auth.AuthEvent
import com.poliba.pwdmanagerpolibaproject.presentation.auth.AuthViewModel
import com.poliba.pwdmanagerpolibaproject.presentation.auth.LoginScreen
import com.poliba.pwdmanagerpolibaproject.presentation.auth.SignupScreen
import com.poliba.pwdmanagerpolibaproject.presentation.generatePwd.GeneratorEvent
import com.poliba.pwdmanagerpolibaproject.presentation.generatePwd.GeneratorViewModel
import com.poliba.pwdmanagerpolibaproject.presentation.home.HomeEvent
import com.poliba.pwdmanagerpolibaproject.presentation.welcome.WelcomeEvent
import com.poliba.pwdmanagerpolibaproject.presentation.home.HomeScreen
import com.poliba.pwdmanagerpolibaproject.presentation.home.HomeViewModel
import com.poliba.pwdmanagerpolibaproject.presentation.home.WelcomeScreen
import com.poliba.pwdmanagerpolibaproject.presentation.profile.ProfileEvent
import com.poliba.pwdmanagerpolibaproject.presentation.welcome.WelcomeViewModel
import com.poliba.pwdmanagerpolibaproject.presentation.profile.ProfileScreen
import com.poliba.pwdmanagerpolibaproject.presentation.profile.ProfileViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(navController: NavHostController) {

  NavHost(
      navController = navController,
      route = Graph.ROOT,
      startDestination = Graph.WELCOME
  ) {

      welcomeNavGraph(navController = navController)

      composable(
          route = Graph.HOME
      ) {
          MainScreenView()
      }
  }
}


@ExperimentalAnimationApi
fun NavGraphBuilder.welcomeNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.WELCOME, startDestination = WelcomeScreen.FirstScreen.route
    ) {
        composable(route = WelcomeScreen.FirstScreen.route) {
            val viewModel = initViewModel<WelcomeViewModel>(
                navController,
            )
            WelcomeScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleWelcomeEvent(
                        event,
                        navController
                    )
                }
            )
        }

        composable(route = AuthScreen.LoginScreen.route) { navBackStackEntry ->
            val stackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.WELCOME) }
            val viewModel = hiltViewModel<AuthViewModel>(stackEntry)
            LoginScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleAuthEvent(
                        event,
                        viewModel,
                        navController
                    )
                }
            )
        }

        composable(route = AuthScreen.SignupScreen.route) {navBackStackEntry ->
            val stackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.WELCOME) }
            val viewModel = hiltViewModel<AuthViewModel>(stackEntry)
            SignupScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleAuthEvent(
                        event,
                        viewModel,
                        navController
                    )
                }
            )
        }
    }
}

@Composable
@ExperimentalAnimationApi
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController,
        route = Graph.HOME,
        startDestination = BottomNavigationItem.Home.route
    ) {

        welcomeNavGraph(navController = navController)

        composable(
            route = BottomNavigationItem.Home.route
        ) { navBackStackEntry ->
            val homeStackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.HOME) }
            val viewModel = hiltViewModel<HomeViewModel>(homeStackEntry)
            HomeScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleHomeEvent(event, viewModel, navController)
                }
            )
        }


        composable(
            route = BottomNavigationItem.GeneratePassword.route
        ) { navBackStackEntry ->
            val stackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.HOME) }
            val viewModel = hiltViewModel<GeneratorViewModel>(stackEntry)
            GeneratorScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleGeneratorEvent(event, viewModel, navController)
                }
            )
        }

        composable(
            route = BottomNavigationItem.Profile.route
        ) { navBackStackEntry ->
            val stackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.HOME) }
            val viewModel = hiltViewModel<ProfileViewModel>(stackEntry)
            ProfileScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleProfileEvent(event, viewModel, navController)
                }
            )
        }


        composable(
            route = HomeScreen.AddPwdScreen.route
        ) { navBackStackEntry ->
            val homeStackEntry =
                remember(navBackStackEntry) { navController.getBackStackEntry(Graph.HOME) }
            val viewModel = hiltViewModel<HomeViewModel>(homeStackEntry)
            AddPasswordScreen(
                state = viewModel.state,
                onEvent = { event ->
                    handleHomeEvent(event, viewModel, navController)
                }
            )
        }

    }
}




object Graph {
    const val ROOT = "root_graph"
    const val WELCOME = "welcome_graph"
    const val HOME = "home_graph"
}


sealed class WelcomeScreen(val route: String) {
    data object FirstScreen : WelcomeScreen(route = Screen.WelcomeScreen.route)
}


sealed class AuthScreen(val route: String) {
    data object LoginScreen : AuthScreen(route = Screen.LoginScreen.route)
    data object SignupScreen : AuthScreen(route = Screen.SignupScreen.route)
}


sealed class HomeScreen(val route: String) {
    data object AddPwdScreen : HomeScreen(route = Screen.AddNewPwdScreen.route)
}


// ----------------------------- handle navigation events
private fun handleWelcomeEvent(
    event: WelcomeEvent,
    navController: NavHostController
) {
    when (event) {
        WelcomeEvent.OnViewPwdClick -> {
            navController.navigate(AuthScreen.LoginScreen.route)
        }

        WelcomeEvent.OnGeneratePwdClick -> {
            navController.navigate(AuthScreen.LoginScreen.route)
        }
        else -> Unit
    }
}


private fun handleAuthEvent(
    event: AuthEvent,
    viewModel: AuthViewModel,
    navController: NavHostController
) {
    when (event) {
        AuthEvent.OnLoginClick -> {
            viewModel.onEvent(event)
        }
        AuthEvent.OnSignupClick -> {
            viewModel.onEvent(event)
        }
        AuthEvent.OnNavigateToSignup -> {
            navController.navigate(AuthScreen.SignupScreen.route)
        }
        AuthEvent.OnNavigateToLogin -> {
            navController.navigate(AuthScreen.LoginScreen.route)
        }
        AuthEvent.OnSignupSuccess, AuthEvent.OnLoginSuccess -> {
            viewModel.onEvent(event)
            navController.navigate(Graph.HOME) {
                popUpTo(Graph.WELCOME) {
                    inclusive = true
                }
            }
        }
        else -> viewModel.onEvent(event)
    }

    // Check authentication state after handling the event
//    if (viewModel.state.isAuthenticated && event !is AuthEvent.OnLogoutClick) {
//        navController.navigate(Graph.HOME) {
//            popUpTo(Graph.WELCOME) {
//                inclusive = true
//            }
//        }
//    }
}



private fun handleProfileEvent(
    event: ProfileEvent,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    when (event) {
        ProfileEvent.OnLogoutClick -> {
            viewModel.onEvent(event)
            navController.navigate(Graph.WELCOME) {
                popUpTo(Graph.HOME) {
                    inclusive = false
                }
            }
        }
    }
}


private fun handleHomeEvent(
    event: HomeEvent,
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    when (event) {

        HomeEvent.OnAddPwdClick -> {
            navController.navigate(HomeScreen.AddPwdScreen.route)
        }

        HomeEvent.OnBack -> {
            navController.popBackStack()
        }

        is HomeEvent.OnSavePassword -> {
            viewModel.handleEvents(event)
            navController.navigate(Graph.HOME) {
                popUpTo(Graph.HOME) {
                    inclusive = true
                }
            }
        }

        else -> viewModel.handleEvents(event)
    }
}


private fun handleGeneratorEvent(
    event: GeneratorEvent,
    viewModel: GeneratorViewModel,
    navController: NavHostController
) {
    when (event) {
        else -> viewModel.handleEvent(event)
    }
}


@Composable
private inline fun <reified T : ViewModel> initViewModel(
    navController: NavHostController? = null,
    backStackEntry: NavBackStackEntry? = null,
    graph: String? = null
): T {
    return if (backStackEntry != null && graph != null && navController != null) {
        hiltViewModel<T>(remember(backStackEntry) { navController.getBackStackEntry(graph) })
    } else {
        hiltViewModel<T>()
    }
}



///-------------------------- todo: added just for test, delete when right page will be created

@Composable
fun testScreen() {
  // Contenuto della schermata Profile
//  Text(text = "Profile Screen")

}


