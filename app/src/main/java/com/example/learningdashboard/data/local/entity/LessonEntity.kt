package com.example.learningdashboard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("courseId")],
)
data class LessonEntity(
    @PrimaryKey val id: Long,
    val courseId: Long,
    val title: String,
    @ColumnInfo(name = "sortOrder") val sortOrder: Int,
    val completed: Boolean,
)
