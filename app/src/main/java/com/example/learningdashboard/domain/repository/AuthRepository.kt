package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.User

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>
}
