package com.example.learningdashboard.domain.model

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonCount: Int,
)
