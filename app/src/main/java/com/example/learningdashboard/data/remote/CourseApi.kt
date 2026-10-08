package com.example.learningdashboard.data.remote

import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.data.remote.dto.LessonDto

interface CourseApi {

    suspend fun fetchCourses(): List<CourseDto>

    suspend fun fetchLessons(courseId: Long): List<LessonDto>
}
