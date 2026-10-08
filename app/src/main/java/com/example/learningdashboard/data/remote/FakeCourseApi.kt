package com.example.learningdashboard.data.remote

import android.content.Context
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.data.remote.dto.LessonDto
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Singleton
class FakeCourseApi @Inject constructor(
    @ApplicationContext private val context: Context,
    private val debugApiControl: DebugApiControl,
) : CourseApi {

    private val json = Json { ignoreUnknownKeys = true }

    private val cachedCourses: List<CourseDto> by lazy {
        json.decodeFromString(readAsset(COURSES_ASSET))
    }

    private val cachedLessons: List<LessonDto> by lazy {
        json.decodeFromString(readAsset(LESSONS_ASSET))
    }

    override suspend fun fetchCourses(): List<CourseDto> = withContext(Dispatchers.IO) {
        delay(RESPONSE_DELAY_MS)
        when (debugApiControl.mode.value) {
            ApiMode.FAILURE -> throw IOException("Simulated course API failure")
            ApiMode.EMPTY -> emptyList()
            ApiMode.NORMAL -> cachedCourses
        }
    }

    override suspend fun fetchLessons(courseId: Long): List<LessonDto> = withContext(Dispatchers.IO) {
        delay(LESSON_RESPONSE_DELAY_MS)
        when (debugApiControl.mode.value) {
            ApiMode.FAILURE -> throw IOException("Simulated lesson API failure")
            ApiMode.EMPTY -> emptyList()
            ApiMode.NORMAL -> cachedLessons.filter { it.courseId == courseId }
        }
    }

    private fun readAsset(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }

    private companion object {
        const val COURSES_ASSET = "courses.json"
        const val LESSONS_ASSET = "lessons.json"
        const val RESPONSE_DELAY_MS = 700L
        const val LESSON_RESPONSE_DELAY_MS = 250L
    }
}
