package com.example.learningdashboard.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun `two of four completed lessons results in 50 percent`() {
        assertEquals(50, ProgressCalculator.calculate(completedLessons = 2, totalLessons = 4))
    }

    @Test
    fun `zero lessons does not divide by zero`() {
        assertEquals(0, ProgressCalculator.calculate(completedLessons = 0, totalLessons = 0))
    }

    @Test
    fun `thirteen of twenty completed lessons results in 65 percent`() {
        assertEquals(65, ProgressCalculator.calculate(completedLessons = 13, totalLessons = 20))
    }

    @Test
    fun `all lessons completed results in 100 percent`() {
        assertEquals(100, ProgressCalculator.calculate(completedLessons = 6, totalLessons = 6))
    }

    @Test
    fun `completed count above total is clamped to 100 percent`() {
        assertEquals(100, ProgressCalculator.calculate(completedLessons = 5, totalLessons = 4))
    }

    @Test
    fun `fractional progress rounds down`() {
        assertEquals(33, ProgressCalculator.calculate(completedLessons = 1, totalLessons = 3))
    }

    @Test
    fun `negative input is clamped to zero`() {
        assertEquals(0, ProgressCalculator.calculate(completedLessons = -1, totalLessons = 4))
    }
}
