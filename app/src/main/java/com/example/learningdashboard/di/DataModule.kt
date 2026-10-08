package com.example.learningdashboard.di

import com.example.learningdashboard.data.local.CourseLocalDataSource
import com.example.learningdashboard.data.local.RoomCourseLocalDataSource
import com.example.learningdashboard.data.remote.AuthApi
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.remote.DebugModeStore
import com.example.learningdashboard.data.remote.FakeAuthApi
import com.example.learningdashboard.data.remote.FakeCourseApi
import com.example.learningdashboard.data.remote.SharedPreferencesDebugModeStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCourseApi(impl: FakeCourseApi): CourseApi

    @Binds
    @Singleton
    abstract fun bindAuthApi(impl: FakeAuthApi): AuthApi

    @Binds
    @Singleton
    abstract fun bindCourseLocalDataSource(impl: RoomCourseLocalDataSource): CourseLocalDataSource

    @Binds
    @Singleton
    abstract fun bindDebugModeStore(impl: SharedPreferencesDebugModeStore): DebugModeStore
}
