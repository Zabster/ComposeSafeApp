package com.zabster.safeappcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для начального экрана с заставкой
 *
 */
class SplashViewModel: ViewModel() {

    var loadingState by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            delay(1000)
            loadingState = false
        }
    }
}