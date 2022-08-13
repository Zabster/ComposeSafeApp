package com.zabster.safeappcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zabster.safeappcompose.di.NavigationDispatcher
import com.zabster.safeappcompose.screens.credential.ui.CreateOrUpdateCredentialScreen
import com.zabster.safeappcompose.screens.login.ui.LoginScreen
import com.zabster.safeappcompose.screens.main.ui.MainScreen
import com.zabster.safeappcompose.screens.settings.SettingsScreen
import com.zabster.safeappcompose.screens.utils.Screen
import com.zabster.safeappcompose.ui.theme.SafeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.loadingState
            }
        }

        setContent {
            SafeAppTheme {
                val navController = rememberNavController()

                Scaffold(
                    scaffoldState = rememberScaffoldState(),
                    backgroundColor = MaterialTheme.colors.background,
                    content = { NavHostView(navController) }
                )
                observeStates(navController)
            }
        }
    }

    private fun observeStates(navController: NavController) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigationDispatcher.navCommandState
                    .collect { command ->
                        command.invoke(navController)
                    }
            }
        }
    }
}

@Composable
fun NavHostView(navController: NavController) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen()
        }
        composable(Screen.MainScreen.route) {
            MainScreen()
        }
        composable(Screen.SettingsScreen.route) {
            SettingsScreen()
        }
        composable(Screen.CreateCredentialScreen.route) {
            CreateOrUpdateCredentialScreen(navController = navController)
        }
        composable(
            Screen.UpdateCredentialScreen("{${Screen.UpdateCredentialScreen.ArgsId}}").route,
            arguments = listOf(navArgument(Screen.UpdateCredentialScreen.ArgsId) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            CreateOrUpdateCredentialScreen(
                id = backStackEntry.arguments?.getLong(Screen.UpdateCredentialScreen.ArgsId),
                navController = navController
            )
        }
    }
}
