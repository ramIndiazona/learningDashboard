package com.example.learningdashboard.data.local

import androidx.room.withTransaction
import com.example.learningdashboard.data.local.dao.CourseDao
import com.example.learningdashboard.data.local.dao.LessonDao
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity
import com.example.learningdashboard.domain.util.ProgressCalculator
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class RoomCourseLocalDataSource @Inject constructor(
    private val database: LearningDatabase,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
) : CourseLocalDataSource {

    override fun observeCourses(): Flow<List<CourseEntity>> = courseDao.observeCourses()

    override fun observeCourse(courseId: Long): Flow<CourseEntity?> = courseDao.observeCourse(courseId)

    override fun observeLessons(courseId: Long): Flow<List<LessonEntity>> = lessonDao.observeLessons(courseId)

    override suspend fun syncCourses(courses: List<CourseEntity>, lessons: List<LessonEntity>) {
        database.withTransaction {
            courses.forEach { incoming ->
                val existing = courseDao.getCourse(incoming.id)
                if (existing == null) {
                    courseDao.insert(incoming)
                } else {
                    courseDao.updateMetadata(
                        courseId = incoming.id,
                        title = incoming.title,
                        instructor = incoming.instructor,
                        lessonCount = incoming.lessonCount,
                    )
                }
            }
            lessonDao.insertAll(lessons)
            courses.forEach { incoming -> refreshProgress(incoming) }
            val incomingIds = courses.map { it.id }
            if (incomingIds.isEmpty()) {
                courseDao.deleteAll()
            } else {
                courseDao.deleteCoursesExcept(incomingIds)
            }
        }
    }

    override suspend fun markLessonCompleted(courseId: Long, lessonId: Long) {
        database.withTransaction {
            lessonDao.updateCompleted(courseId = courseId, lessonId = lessonId, completed = true)
            val course = courseDao.getCourse(courseId) ?: return@withTransaction
            refreshProgress(course)
        }
    }

    private suspend fun refreshProgress(course: CourseEntity) {
        val lessons = lessonDao.getLessons(course.id)
        val progress = if (lessons.isEmpty()) {
            course.progress
        } else {
            ProgressCalculator.calculate(
                completedLessons = lessons.count { it.completed },
                totalLessons = lessons.size,
            )
        }
        courseDao.updateProgress(course.id, progress)
    }
}
