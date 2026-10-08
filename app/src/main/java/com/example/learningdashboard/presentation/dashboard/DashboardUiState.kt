package com.example.learningdashboard.presentation.dashboard

import com.example.learningdashboard.domain.model.Course

sealed interface DashboardUiState {

    data object Loading : DashboardUiState

    data class Success(
        val courses: List<Course>,
        val isRefreshing: Boolean = false,
        val isShowingCachedData: Boolean = false,
    ) : DashboardUiState

    data object Empty : DashboardUiState

    data class Error(val message: String) : DashboardUiState
}
