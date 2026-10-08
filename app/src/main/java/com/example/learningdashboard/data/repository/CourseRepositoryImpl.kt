package com.example.learningdashboard.data.repository

import android.util.Log
import com.example.learningdashboard.data.local.CourseLocalDataSource
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.mapper.toDomain
import com.example.learningdashboard.data.mapper.toEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.domain.repository.CourseRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseApi: CourseApi,
    private val localDataSource: CourseLocalDataSource,
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> =
        localDataSource.observeCourses().map { entities -> entities.map { it.toDomain() } }

    override fun observeCourse(courseId: Long): Flow<Course?> =
        localDataSource.observeCourse(courseId).map { it?.toDomain() }

    override fun observeLessons(courseId: Long): Flow<List<Lesson>> =
        localDataSource.observeLessons(courseId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshCourses(): Result<Unit> {
        return try {
            val courses = courseApi.fetchCourses()
            val lessons = courses.flatMap { course -> courseApi.fetchLessons(course.id) }
            localDataSource.syncCourses(
                courses = courses.map { it.toEntity() },
                lessons = lessons.map { it.toEntity() },
            )
            Result.success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Log.e(TAG, "Failed to refresh courses", error)
            Result.failure(error)
        }
    }

    override suspend fun markLessonCompleted(courseId: Long, lessonId: Long): Result<Unit> {
        return try {
            localDataSource.markLessonCompleted(courseId = courseId, lessonId = lessonId)
            Result.success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Log.e(TAG, "Failed to mark lesson completed", error)
            Result.failure(error)
        }
    }

    private companion object {
        const val TAG = "CourseRepository"
    }
}
