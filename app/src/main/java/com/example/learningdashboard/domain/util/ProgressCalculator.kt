package com.example.learningdashboard.domain.util

object ProgressCalculator {

    fun calculate(completedLessons: Int, totalLessons: Int): Int {
        if (totalLessons <= 0 || completedLessons <= 0) return 0
        return ((completedLessons * 100) / totalLessons).coerceIn(0, 100)
    }
}
