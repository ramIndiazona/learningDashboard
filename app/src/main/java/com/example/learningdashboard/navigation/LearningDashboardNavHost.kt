package com.example.learningdashboard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningdashboard.presentation.course_details.CourseDetailsScreen
import com.example.learningdashboard.presentation.dashboard.DashboardScreen
import com.example.learningdashboard.presentation.login.LoginScreen

@Composable
fun LearningDashboardNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onCourseClick = { courseId ->
                    navController.navigate(Routes.courseDetails(courseId))
                },
            )
        }
        composable(
            route = Routes.COURSE_DETAILS,
            arguments = listOf(
                navArgument(Routes.ARG_COURSE_ID) { type = NavType.LongType },
            ),
        ) {
            CourseDetailsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
