package com.example.learningdashboard.di

import android.content.Context
import androidx.room.Room
import com.example.learningdashboard.data.local.LearningDatabase
import com.example.learningdashboard.data.local.dao.CourseDao
import com.example.learningdashboard.data.local.dao.LessonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LearningDatabase =
        Room.databaseBuilder(context, LearningDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    fun provideCourseDao(database: LearningDatabase): CourseDao = database.courseDao()

    @Provides
    fun provideLessonDao(database: LearningDatabase): LessonDao = database.lessonDao()

    private const val DATABASE_NAME = "learning_dashboard.db"
}
