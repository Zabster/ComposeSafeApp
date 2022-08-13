package com.zabster.safeappcompose.screens.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleEvent(onEvent: (Lifecycle.Event) -> Unit) {
    val eventCollector = rememberUpdatedState(newValue = onEvent)
    val lifecycle = rememberUpdatedState(newValue = LocalLifecycleOwner.current)
    DisposableEffect(key1 = lifecycle) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            eventCollector.value(event)
        }
        lifecycle.value.lifecycle.addObserver(observer)
        onDispose {
            lifecycle.value.lifecycle.removeObserver(observer)
        }
    }
}