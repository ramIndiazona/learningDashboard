package com.example.learningdashboard.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    @SerialName("lessons") val lessonCount: Int,
)
