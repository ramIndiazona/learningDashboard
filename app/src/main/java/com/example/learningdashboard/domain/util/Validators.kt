package com.example.learningdashboard.domain.util

object Validators {

    const val MIN_PASSWORD_LENGTH = 6

    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_PATTERN.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH
}
