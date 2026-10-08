package com.example.learningdashboard.data.local

import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

interface CourseLocalDataSource {

    fun observeCourses(): Flow<List<CourseEntity>>

    fun observeCourse(courseId: Long): Flow<CourseEntity?>

    fun observeLessons(courseId: Long): Flow<List<LessonEntity>>

    suspend fun syncCourses(courses: List<CourseEntity>, lessons: List<LessonEntity>)

    suspend fun markLessonCompleted(courseId: Long, lessonId: Long)
}
