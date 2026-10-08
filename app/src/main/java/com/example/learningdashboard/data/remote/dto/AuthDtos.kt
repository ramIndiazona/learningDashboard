package com.example.learningdashboard.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val userId: Long,
    val email: String,
)
