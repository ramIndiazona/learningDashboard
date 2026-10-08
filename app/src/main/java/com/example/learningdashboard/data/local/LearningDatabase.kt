package com.example.learningdashboard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learningdashboard.data.local.dao.CourseDao
import com.example.learningdashboard.data.local.dao.LessonDao
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity

@Database(
    entities = [CourseEntity::class, LessonEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class LearningDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao

    abstract fun lessonDao(): LessonDao
}
