package com.example.learningdashboard.presentation.login

import com.example.learningdashboard.domain.error.InvalidCredentialsException
import com.example.learningdashboard.domain.model.User
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `blank credentials show validation errors without calling the repository`() = runTest {
        val authRepository = FakeAuthRepository(Result.success(User(1, "user@example.com")))
        val viewModel = LoginViewModel(authRepository)

        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertEquals("Email is required", state.emailError)
        assertEquals("Password is required", state.passwordError)
        assertEquals(0, authRepository.loginCalls)
    }

    @Test
    fun `malformed email shows inline validation error`() = runTest {
        val authRepository = FakeAuthRepository(Result.success(User(1, "user@example.com")))
        val viewModel = LoginViewModel(authRepository)

        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("secret123")
        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertEquals("Enter a valid email address", state.emailError)
        assertNull(state.passwordError)
        assertEquals(0, authRepository.loginCalls)
    }

    @Test
    fun `valid credentials navigate to the dashboard once submission succeeds`() = runTest {
        val authRepository = FakeAuthRepository(Result.success(User(1, "user@example.com")))
        val viewModel = LoginViewModel(authRepository)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret123")
        viewModel.onLoginClick()

        val effect = viewModel.effects.first()
        assertEquals(LoginUiEffect.NavigateToDashboard, effect)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(1, authRepository.loginCalls)
    }

    @Test
    fun `rejected credentials surface a user friendly error`() = runTest {
        val authRepository = FakeAuthRepository(Result.failure(InvalidCredentialsException()))
        val viewModel = LoginViewModel(authRepository)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("wrongpass")
        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertEquals("Invalid email or password", state.errorMessage)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `repeated login taps while submitting are ignored`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val authRepository = FakeAuthRepository(
            result = Result.success(User(1, "user@example.com")),
            gate = gate,
        )
        val viewModel = LoginViewModel(authRepository)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret123")
        viewModel.onLoginClick()
        viewModel.onLoginClick()

        assertTrue(viewModel.uiState.value.isSubmitting)
        assertEquals(1, authRepository.loginCalls)
    }
}

private class FakeAuthRepository(
    private val result: Result<User>,
    private val gate: CompletableDeferred<Unit>? = null,
) : AuthRepository {

    var loginCalls = 0
        private set

    override suspend fun login(email: String, password: String): Result<User> {
        loginCalls++
        gate?.await()
        return result
    }
}
