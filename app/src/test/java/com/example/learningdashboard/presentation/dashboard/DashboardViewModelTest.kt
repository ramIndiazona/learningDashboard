package com.example.learningdashboard.presentation.dashboard

import com.example.learningdashboard.data.remote.ApiMode
import com.example.learningdashboard.data.remote.DebugApiControl
import com.example.learningdashboard.data.remote.DebugModeStore
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.util.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val debugApiControl = DebugApiControl(InMemoryDebugModeStore())

    @Test
    fun `state moves from loading to success when refresh returns courses`() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(courses = listOf(sampleCourse)),
            debugApiControl = debugApiControl,
        )

        assertTrue(viewModel.uiState.value is DashboardUiState.Loading)

        val state = viewModel.uiState.first { it !is DashboardUiState.Loading }

        assertTrue(state is DashboardUiState.Success)
        assertEquals(listOf(sampleCourse), (state as DashboardUiState.Success).courses)
        assertFalse(state.isShowingCachedData)
    }

    @Test
    fun `state moves from loading to error when refresh fails with empty cache`() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(refreshError = IOException("offline")),
            debugApiControl = debugApiControl,
        )

        val state = viewModel.uiState.first { it !is DashboardUiState.Loading }

        assertTrue(state is DashboardUiState.Error)
        assertEquals(
            "No network connection. Please try again.",
            (state as DashboardUiState.Error).message,
        )
    }

    @Test
    fun `state moves from loading to empty when refresh succeeds without courses`() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(courses = emptyList()),
            debugApiControl = debugApiControl,
        )

        val state = viewModel.uiState.first { it !is DashboardUiState.Loading }

        assertTrue(state is DashboardUiState.Empty)
    }

    @Test
    fun `refresh failure with cached courses keeps success state with cached data flag`() = runTest {
        val viewModel = DashboardViewModel(
            courseRepository = FakeCourseRepository(
                courses = listOf(sampleCourse),
                refreshError = IOException("offline"),
            ),
            debugApiControl = debugApiControl,
        )

        val state = viewModel.uiState.first { it !is DashboardUiState.Loading }

        assertTrue(state is DashboardUiState.Success)
        assertTrue((state as DashboardUiState.Success).isShowingCachedData)
    }

    private val sampleCourse = Course(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        progress = 65,
        lessonCount = 20,
    )
}

private class InMemoryDebugModeStore : DebugModeStore {

    override fun read(): ApiMode = ApiMode.NORMAL

    override fun write(mode: ApiMode) = Unit
}

private class FakeCourseRepository(
    private val courses: List<Course> = emptyList(),
    private val refreshError: Exception? = null,
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> = MutableStateFlow(courses)

    override fun observeCourse(courseId: Long): Flow<Course?> =
        MutableStateFlow(courses.find { it.id == courseId })

    override fun observeLessons(courseId: Long): Flow<List<Lesson>> = flowOf(emptyList())

    override suspend fun refreshCourses(): Result<Unit> =
        refreshError?.let { Result.failure(it) } ?: Result.success(Unit)

    override suspend fun markLessonCompleted(courseId: Long, lessonId: Long): Result<Unit> =
        Result.success(Unit)
}
