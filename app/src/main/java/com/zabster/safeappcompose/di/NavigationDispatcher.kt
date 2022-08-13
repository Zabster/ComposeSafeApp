package com.zabster.safeappcompose.di

import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

private typealias NavCommand = (NavController) -> Unit

@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {

    private val _navCommandState = Channel<NavCommand>(capacity = 0)
    val navCommandState = _navCommandState.receiveAsFlow()

    suspend fun emit(command: NavCommand) {
        _navCommandState.send(command)
    }
}