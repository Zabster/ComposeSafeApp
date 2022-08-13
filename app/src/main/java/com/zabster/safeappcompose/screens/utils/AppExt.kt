package com.zabster.safeappcompose.screens.utils

private const val ZERO_LONG = 0L
private const val ZERO_INT = 0

fun String?.runIfNotEmpty(block: (String) -> String) = if (isNullOrBlank()) this else block(this)

fun Long?.orZero() = this ?: ZERO_LONG
fun Int?.orZero() = this ?: ZERO_INT