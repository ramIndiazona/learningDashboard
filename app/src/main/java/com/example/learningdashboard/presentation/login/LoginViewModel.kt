package com.example.learningdashboard.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.util.Validators
import com.example.learningdashboard.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LoginUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null, errorMessage = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = null, errorMessage = null)
        }
    }

    fun onLoginClick() {
        val currentState = _uiState.value
        if (currentState.isSubmitting) return

        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            authRepository.login(
                email = currentState.email.trim(),
                password = currentState.password,
            ).onSuccess {
                _uiState.update { it.copy(isSubmitting = false) }
                _effects.send(LoginUiEffect.NavigateToDashboard)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !Validators.isValidEmail(email) -> "Enter a valid email address"
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        !Validators.isValidPassword(password) ->
            "Password must be at least ${Validators.MIN_PASSWORD_LENGTH} characters"
        else -> null
    }
}
