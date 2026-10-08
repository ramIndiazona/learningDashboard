package com.example.learningdashboard.presentation.course_details

import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson

sealed interface CourseDetailsUiState {

    data object Loading : CourseDetailsUiState

    data class Content(
        val course: Course,
        val lessons: List<Lesson>,
    ) : CourseDetailsUiState

    data class Error(val message: String) : CourseDetailsUiState
}
