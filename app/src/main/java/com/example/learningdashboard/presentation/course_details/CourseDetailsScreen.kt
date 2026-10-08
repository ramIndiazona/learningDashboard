package com.example.learningdashboard.presentation.course_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    onBack: () -> Unit,
    viewModel: CourseDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.actionErrors.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val topBarTitle = when (val current = state) {
        is CourseDetailsUiState.Content -> current.course.title
        else -> stringResource(R.string.course_details_title)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when (val current = state) {
            is CourseDetailsUiState.Loading -> LoadingContent(padding = innerPadding)
            is CourseDetailsUiState.Error -> ErrorContent(
                message = current.message,
                padding = innerPadding,
            )
            is CourseDetailsUiState.Content -> Content(
                state = current,
                padding = innerPadding,
                onMarkCompleted = viewModel::markLessonCompleted,
            )
        }
    }
}

@Composable
private fun LoadingContent(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun Content(
    state: CourseDetailsUiState.Content,
    padding: PaddingValues,
    onMarkCompleted: (Long) -> Unit,
) {
    val completedLessons = state.lessons.count { it.completed }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item(key = "progress-header") {
            ProgressHeader(
                courseTitle = state.course.title,
                instructor = state.course.instructor,
                progress = state.course.progress,
                completedLessons = completedLessons,
                totalLessons = state.lessons.size,
            )
        }
        item(key = "lesson-divider") {
            HorizontalDivider()
        }
        items(
            items = state.lessons,
            key = { lesson -> "lesson-${lesson.id}" },
        ) { lesson ->
            LessonRow(
                lesson = lesson,
                onMarkCompleted = { onMarkCompleted(lesson.id) },
            )
        }
    }
}

@Composable
private fun ProgressHeader(
    courseTitle: String,
    instructor: String,
    progress: Int,
    completedLessons: Int,
    totalLessons: Int,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = courseTitle,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = instructor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.course_progress_label),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.course_progress_percent, progress),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pluralStringResource(
                R.plurals.course_lessons_summary,
                totalLessons,
                completedLessons,
                totalLessons,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LessonRow(
    lesson: Lesson,
    onMarkCompleted: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Icon(
            imageVector = if (lesson.completed) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (lesson.completed) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (lesson.completed) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Text(
                text = if (lesson.completed) {
                    stringResource(R.string.lesson_completed)
                } else {
                    stringResource(R.string.lesson_pending)
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            )
        }
        if (!lesson.completed) {
            TextButton(onClick = onMarkCompleted) {
                Text(stringResource(R.string.lesson_mark_complete))
            }
        }
    }
}
