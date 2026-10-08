package com.example.learningdashboard.navigation

object Routes {

    const val LOGIN = "login"

    const val DASHBOARD = "dashboard"

    const val COURSE_DETAILS = "course/{courseId}"

    const val ARG_COURSE_ID = "courseId"

    fun courseDetails(courseId: Long): String = "course/$courseId"
}
