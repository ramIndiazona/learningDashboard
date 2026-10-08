package com.example.learningdashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learningdashboard.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY id")
    fun observeCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun observeCourse(courseId: Long): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourse(courseId: Long): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity)

    @Query(
        "UPDATE courses SET title = :title, instructor = :instructor, " +
            "lessonCount = :lessonCount WHERE id = :courseId"
    )
    suspend fun updateMetadata(courseId: Long, title: String, instructor: String, lessonCount: Int)

    @Query("UPDATE courses SET progress = :progress WHERE id = :courseId")
    suspend fun updateProgress(courseId: Long, progress: Int)

    @Query("DELETE FROM courses WHERE id NOT IN (:courseIds)")
    suspend fun deleteCoursesExcept(courseIds: List<Long>)

    @Query("DELETE FROM courses")
    suspend fun deleteAll()
}
