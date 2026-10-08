package com.example.learningdashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.data.remote.ApiMode
import com.example.learningdashboard.data.remote.DebugApiControl
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val debugApiControl: DebugApiControl,
) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Idle)

    val debugApiMode: StateFlow<ApiMode> = debugApiControl.mode

    val uiState: StateFlow<DashboardUiState> = combine(
        courseRepository.observeCourses(),
        refreshStatus,
    ) { courses, status ->
        status.toUiState(courses)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = DashboardUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() {
        if (refreshStatus.value is RefreshStatus.InFlight) return
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.InFlight
            val result = courseRepository.refreshCourses()
            refreshStatus.value = if (result.isSuccess) {
                RefreshStatus.Succeeded
            } else {
                RefreshStatus.Failed(
                    result.exceptionOrNull()?.toUserMessage() ?: GENERIC_ERROR_MESSAGE,
                )
            }
        }
    }

    fun cycleDebugApiMode() {
        debugApiControl.cycleMode()
        refresh()
    }

    private fun RefreshStatus.toUiState(courses: List<Course>): DashboardUiState = when {
        courses.isEmpty() && this is RefreshStatus.Failed -> DashboardUiState.Error(message)
        courses.isEmpty() && this is RefreshStatus.Succeeded -> DashboardUiState.Empty
        courses.isEmpty() -> DashboardUiState.Loading
        else -> DashboardUiState.Success(
            courses = courses,
            isRefreshing = this is RefreshStatus.InFlight,
            isShowingCachedData = this is RefreshStatus.Failed,
        )
    }

    private sealed interface RefreshStatus {

        data object Idle : RefreshStatus

        data object InFlight : RefreshStatus

        data object Succeeded : RefreshStatus

        data class Failed(val message: String) : RefreshStatus
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."
    }
}
