package com.example.learningdashboard.presentation.course_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.navigation.Routes
import com.example.learningdashboard.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val courseId: Long = savedStateHandle[Routes.ARG_COURSE_ID] ?: INVALID_COURSE_ID

    val uiState: StateFlow<CourseDetailsUiState> = combine(
        courseRepository.observeCourse(courseId),
        courseRepository.observeLessons(courseId),
    ) { course, lessons ->
        if (course == null) {
            CourseDetailsUiState.Error(COURSE_NOT_FOUND_MESSAGE)
        } else {
            CourseDetailsUiState.Content(course = course, lessons = lessons)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CourseDetailsUiState.Loading,
    )

    private val _actionErrors = Channel<String>(Channel.BUFFERED)
    val actionErrors: Flow<String> = _actionErrors.receiveAsFlow()

    fun markLessonCompleted(lessonId: Long) {
        viewModelScope.launch {
            courseRepository.markLessonCompleted(courseId = courseId, lessonId = lessonId)
                .onFailure { error ->
                    _actionErrors.send(error.toUserMessage())
                }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val INVALID_COURSE_ID = -1L
        const val COURSE_NOT_FOUND_MESSAGE = "Course not found"
    }
}
