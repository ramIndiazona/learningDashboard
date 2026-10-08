package com.example.learningdashboard.di

import com.example.learningdashboard.data.repository.AuthRepositoryImpl
import com.example.learningdashboard.data.repository.CourseRepositoryImpl
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(impl: CourseRepositoryImpl): CourseRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
