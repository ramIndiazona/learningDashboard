package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.local.CourseLocalDataSource
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.data.remote.dto.LessonDto
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseRepositoryImplTest {

    @Test
    fun `remote failure with cached data still exposes cached courses`() = runTest {
        val repository = CourseRepositoryImpl(
            courseApi = FakeCourseApi(failure = IOException("network unavailable")),
            localDataSource = FakeCourseLocalDataSource(
                initialCourses = listOf(cachedCourse),
            ),
        )

        val refreshResult = repository.refreshCourses()

        assertTrue(refreshResult.isFailure)
        val courses = repository.observeCourses().first()
        assertEquals(1, courses.size)
        assertEquals("Python Programming", courses.first().title)
    }

    @Test
    fun `remote failure with empty cache keeps cache empty`() = runTest {
        val repository = CourseRepositoryImpl(
            courseApi = FakeCourseApi(failure = IOException("network unavailable")),
            localDataSource = FakeCourseLocalDataSource(),
        )

        val refreshResult = repository.refreshCourses()

        assertTrue(refreshResult.isFailure)
        assertEquals(0, repository.observeCourses().first().size)
    }

    @Test
    fun `successful refresh persists remote courses and lessons locally`() = runTest {
        val repository = CourseRepositoryImpl(
            courseApi = FakeCourseApi(
                courses = listOf(CourseDto(1, "Python Programming", "John Smith", 65, 20)),
                lessonsByCourse = mapOf(
                    1L to listOf(LessonDto(101, 1, "Introduction", 1, completed = true)),
                ),
            ),
            localDataSource = FakeCourseLocalDataSource(),
        )

        val refreshResult = repository.refreshCourses()

        assertTrue(refreshResult.isSuccess)
        val courses = repository.observeCourses().first()
        assertEquals(1, courses.size)
        assertEquals("Python Programming", courses.first().title)
        val lessons = repository.observeLessons(1).first()
        assertEquals(1, lessons.size)
        assertTrue(lessons.first().completed)
    }

    private val cachedCourse = CourseEntity(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        progress = 65,
        lessonCount = 20,
    )
}

private class FakeCourseApi(
    private val courses: List<CourseDto> = emptyList(),
    private val lessonsByCourse: Map<Long, List<LessonDto>> = emptyMap(),
    private val failure: Exception? = null,
) : CourseApi {

    override suspend fun fetchCourses(): List<CourseDto> {
        failure?.let { throw it }
        return courses
    }

    override suspend fun fetchLessons(courseId: Long): List<LessonDto> {
        failure?.let { throw it }
        return lessonsByCourse[courseId].orEmpty()
    }
}

private class FakeCourseLocalDataSource(
    initialCourses: List<CourseEntity> = emptyList(),
    initialLessons: List<LessonEntity> = emptyList(),
) : CourseLocalDataSource {

    private val courses = MutableStateFlow(initialCourses.associateBy { it.id })
    private val lessons = MutableStateFlow(initialLessons.groupBy { it.courseId })

    override fun observeCourses(): Flow<List<CourseEntity>> =
        courses.map { values -> values.values.sortedBy { it.id } }

    override fun observeCourse(courseId: Long): Flow<CourseEntity?> =
        courses.map { values -> values[courseId] }

    override fun observeLessons(courseId: Long): Flow<List<LessonEntity>> =
        lessons.map { values -> values[courseId].orEmpty().sortedBy { it.sortOrder } }

    override suspend fun syncCourses(courses: List<CourseEntity>, lessons: List<LessonEntity>) {
        val incomingIds = courses.map { it.id }.toSet()
        this.courses.update { courses.associateBy { it.id } }
        this.lessons.update { current ->
            val retained = current.filterKeys { it in incomingIds }.toMutableMap()
            lessons.groupBy { it.courseId }.forEach { (courseId, courseLessons) ->
                val existing = retained[courseId].orEmpty().associateBy { it.id }
                retained[courseId] =
                    (existing + courseLessons.filterNot { existing.containsKey(it.id) }.associateBy { it.id })
                        .values
                        .sortedBy { it.sortOrder }
            }
            retained
        }
    }

    override suspend fun markLessonCompleted(courseId: Long, lessonId: Long) {
        lessons.update { current ->
            val courseLessons = current[courseId] ?: return@update current
            current + (courseId to courseLessons.map {
                if (it.id == lessonId) it.copy(completed = true) else it
            })
        }
    }
}
