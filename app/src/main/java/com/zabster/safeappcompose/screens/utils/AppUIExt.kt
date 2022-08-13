package com.zabster.safeappcompose.screens.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier

fun Modifier.imeInsets(scrollState: ScrollState) = this
    .statusBarsPadding()
    .navigationBarsPadding()
    .verticalScroll(scrollState)