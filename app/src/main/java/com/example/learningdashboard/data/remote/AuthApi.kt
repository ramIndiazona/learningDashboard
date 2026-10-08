package com.example.learningdashboard.data.remote

import com.example.learningdashboard.data.remote.dto.LoginResponseDto

interface AuthApi {

    suspend fun login(email: String, password: String): LoginResponseDto
}
