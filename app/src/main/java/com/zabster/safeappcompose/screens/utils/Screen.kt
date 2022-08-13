package com.zabster.safeappcompose.screens.utils

/**
 * Экраны приложения
 *
 * @property route имя для навигации
 */
sealed class Screen(val route: String) {

    /**
     * Экран авторизации
     */
    object LoginScreen : Screen("login_screen")

    object MainScreen : Screen("main_screen")

    object SettingsScreen : Screen("settings_screen")

    object CreateCredentialScreen : Screen("create_credential_screen")

    data class UpdateCredentialScreen(val id: String) : Screen("update_credential_screen/$id") {
        companion object {
            const val ArgsId: String = "id"
        }
    }
}