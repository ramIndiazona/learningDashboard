package com.example.learningdashboard.domain.model

data class Lesson(
    val id: Long,
    val courseId: Long,
    val title: String,
    val sortOrder: Int,
    val completed: Boolean,
)
