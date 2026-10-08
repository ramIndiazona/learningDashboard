package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun observeCourses(): Flow<List<Course>>

    fun observeCourse(courseId: Long): Flow<Course?>

    fun observeLessons(courseId: Long): Flow<List<Lesson>>

    suspend fun refreshCourses(): Result<Unit>

    suspend fun markLessonCompleted(courseId: Long, lessonId: Long): Result<Unit>
}
