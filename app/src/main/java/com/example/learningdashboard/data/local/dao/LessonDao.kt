package com.example.learningdashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learningdashboard.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY sortOrder")
    fun observeLessons(courseId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY sortOrder")
    suspend fun getLessons(courseId: Long): List<LessonEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET completed = :completed WHERE id = :lessonId AND courseId = :courseId")
    suspend fun updateCompleted(courseId: Long, lessonId: Long, completed: Boolean)
}
