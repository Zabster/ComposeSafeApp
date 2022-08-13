package com.zabster.safeappcompose.screens.common.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zabster.safeappcompose.di.NavigationDispatcher
import com.zabster.safeappcompose.screens.utils.Screen
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val navigationDispatcher: NavigationDispatcher) : ViewModel() {

    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            navigationDispatcher.emit { navController ->
                navController.navigate(screen.route)
            }
        }
    }

    fun navigateSingleTo(screen: Screen) {
        viewModelScope.launch {
            navigationDispatcher.emit { navController ->
                navController.popBackStack()
                navController.navigate(screen.route) {
                    popUpTo(screen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}