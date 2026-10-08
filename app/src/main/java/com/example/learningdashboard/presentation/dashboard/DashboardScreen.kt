package com.example.learningdashboard.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.BuildConfig
import com.example.learningdashboard.R
import com.example.learningdashboard.data.remote.ApiMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onCourseClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val debugMode by viewModel.debugApiMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.cd_refresh),
                        )
                    }
                    if (BuildConfig.DEBUG) {
                        IconButton(onClick = viewModel::cycleDebugApiMode) {
                            Icon(
                                imageVector = debugMode.icon(),
                                contentDescription = stringResource(
                                    R.string.cd_debug_api_mode,
                                    stringResource(debugMode.labelRes()),
                                ),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val current = state) {
                is DashboardUiState.Loading -> LoadingContent()
                is DashboardUiState.Error -> ErrorContent(
                    message = current.message,
                    onRetry = viewModel::refresh,
                )
                is DashboardUiState.Empty -> EmptyContent(onRetry = viewModel::refresh)
                is DashboardUiState.Success -> SuccessContent(
                    state = current,
                    onCourseClick = onCourseClick,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    StateMessage(
        icon = Icons.Outlined.CloudOff,
        title = message,
        actionLabel = stringResource(R.string.dashboard_retry),
        onAction = onRetry,
    )
}

@Composable
private fun EmptyContent(onRetry: () -> Unit) {
    StateMessage(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        title = stringResource(R.string.dashboard_empty_title),
        subtitle = stringResource(R.string.dashboard_empty_message),
        actionLabel = stringResource(R.string.dashboard_retry),
        onAction = onRetry,
    )
}

@Composable
private fun StateMessage(
    icon: ImageVector,
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    subtitle: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAction) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(actionLabel)
        }
    }
}

@Composable
private fun SuccessContent(
    state: DashboardUiState.Success,
    onCourseClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.isShowingCachedData) {
            item(key = "offline-banner") {
                OfflineBanner()
            }
        }
        if (state.isRefreshing) {
            item(key = "refresh-indicator") {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        items(
            items = state.courses,
            key = { course -> "course-${course.id}" },
        ) { course ->
            CourseCard(
                course = course,
                onOpenCourse = { onCourseClick(course.id) },
            )
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.dashboard_offline_notice),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

private fun ApiMode.icon(): ImageVector = when (this) {
    ApiMode.NORMAL -> Icons.Outlined.CloudDone
    ApiMode.FAILURE -> Icons.Outlined.CloudOff
    ApiMode.EMPTY -> Icons.Outlined.Inbox
}

private fun ApiMode.labelRes(): Int = when (this) {
    ApiMode.NORMAL -> R.string.debug_mode_normal
    ApiMode.FAILURE -> R.string.debug_mode_failure
    ApiMode.EMPTY -> R.string.debug_mode_empty
}
