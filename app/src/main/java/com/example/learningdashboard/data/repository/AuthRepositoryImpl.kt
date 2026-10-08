package com.example.learningdashboard.data.repository

import android.util.Log
import com.example.learningdashboard.data.mapper.toDomain
import com.example.learningdashboard.data.remote.AuthApi
import com.example.learningdashboard.domain.error.InvalidCredentialsException
import com.example.learningdashboard.domain.model.User
import com.example.learningdashboard.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            Result.success(authApi.login(email = email, password = password).toDomain())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: InvalidCredentialsException) {
            Log.w(TAG, "Login rejected: invalid credentials")
            Result.failure(error)
        } catch (error: Exception) {
            Log.e(TAG, "Login failed", error)
            Result.failure(error)
        }
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
