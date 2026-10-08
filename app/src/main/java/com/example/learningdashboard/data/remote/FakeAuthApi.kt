package com.example.learningdashboard.data.remote

import com.example.learningdashboard.data.remote.dto.LoginResponseDto
import com.example.learningdashboard.domain.error.InvalidCredentialsException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class FakeAuthApi @Inject constructor() : AuthApi {

    override suspend fun login(email: String, password: String): LoginResponseDto {
        delay(RESPONSE_DELAY_MS)
        val normalizedEmail = email.trim()
        if (normalizedEmail.equals(DEMO_EMAIL, ignoreCase = true) && password == DEMO_PASSWORD) {
            return LoginResponseDto(userId = 1L, email = normalizedEmail)
        }
        throw InvalidCredentialsException()
    }

    private companion object {
        const val RESPONSE_DELAY_MS = 1_200L
        const val DEMO_EMAIL = "demo@learning.app"
        const val DEMO_PASSWORD = "demo1234"
    }
}
