package com.example.learningdashboard.data.mapper

import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.data.remote.dto.LessonDto
import com.example.learningdashboard.data.remote.dto.LoginResponseDto
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.domain.model.User

fun CourseDto.toEntity(): CourseEntity = CourseEntity(
    id = id,
    title = title,
    instructor = instructor,
    progress = progress,
    lessonCount = lessonCount,
)

fun CourseEntity.toDomain(): Course = Course(
    id = id,
    title = title,
    instructor = instructor,
    progress = progress,
    lessonCount = lessonCount,
)

fun LessonDto.toEntity(): LessonEntity = LessonEntity(
    id = id,
    courseId = courseId,
    title = title,
    sortOrder = sortOrder,
    completed = completed,
)

fun LessonEntity.toDomain(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    sortOrder = sortOrder,
    completed = completed,
)

fun LoginResponseDto.toDomain(): User = User(
    id = userId,
    email = email,
)
