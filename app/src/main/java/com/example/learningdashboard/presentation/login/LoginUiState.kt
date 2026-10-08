package com.example.learningdashboard.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginUiEffect {

    data object NavigateToDashboard : LoginUiEffect
}
