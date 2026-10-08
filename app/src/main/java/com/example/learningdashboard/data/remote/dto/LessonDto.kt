package com.example.learningdashboard.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    val id: Long,
    val courseId: Long,
    val title: String,
    val sortOrder: Int,
    val completed: Boolean,
)
