package com.example.learningdashboard.data.remote

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DebugModeStore {

    fun read(): ApiMode

    fun write(mode: ApiMode)
}

@Singleton
class SharedPreferencesDebugModeStore @Inject constructor(
    @ApplicationContext context: Context,
) : DebugModeStore {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun read(): ApiMode {
        val stored = prefs.getString(KEY_API_MODE, null) ?: return ApiMode.NORMAL
        return ApiMode.entries.firstOrNull { it.name == stored } ?: ApiMode.NORMAL
    }

    override fun write(mode: ApiMode) {
        prefs.edit { putString(KEY_API_MODE, mode.name) }
    }

    private companion object {
        const val FILE_NAME = "debug_api_mode"
        const val KEY_API_MODE = "api_mode"
    }
}

@Singleton
class DebugApiControl @Inject constructor(
    private val modeStore: DebugModeStore,
) {

    private val _mode = MutableStateFlow(modeStore.read())
    val mode: StateFlow<ApiMode> = _mode.asStateFlow()

    fun cycleMode() {
        _mode.update { current ->
            val modes = ApiMode.entries
            modes[(current.ordinal + 1) % modes.size]
        }
        modeStore.write(_mode.value)
    }
}
