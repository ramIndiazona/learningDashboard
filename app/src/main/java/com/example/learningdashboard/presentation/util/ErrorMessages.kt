package com.example.learningdashboard.presentation.util

import com.example.learningdashboard.domain.error.InvalidCredentialsException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is InvalidCredentialsException -> message ?: "Invalid email or password"
    is IOException -> "No network connection. Please try again."
    else -> "Something went wrong. Please try again."
}
