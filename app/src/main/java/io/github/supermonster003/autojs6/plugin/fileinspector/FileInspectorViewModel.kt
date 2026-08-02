package io.github.supermonster003.autojs6.plugin.fileinspector

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.supermonster003.autojs6.plugin.fileinspector.core.FileInspectionEngine
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionPolicy
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectorSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal sealed interface FileInspectorUiState {
    data object Idle : FileInspectorUiState
    data class Running(val bytesRead: Long) : FileInspectorUiState
    data class Complete(val report: InspectionReport) : FileInspectorUiState
    data object Canceled : FileInspectorUiState
    data class Failed(val error: Throwable) : FileInspectorUiState
}

internal class FileInspectorViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<FileInspectorUiState>(FileInspectorUiState.Idle)
    val state: StateFlow<FileInspectorUiState> = _state.asStateFlow()

    private var activeJob: Job? = null
    private var generation = 0L

    fun inspect(request: FileInspectionRequest) {
        if (_state.value != FileInspectorUiState.Idle) return
        launchInspection(request)
    }

    fun retry(request: FileInspectionRequest) {
        launchInspection(request)
    }

    fun cancel() {
        generation += 1L
        activeJob?.cancel(CancellationException("Inspection canceled by user"))
        _state.value = FileInspectorUiState.Canceled
    }

    fun completedReport(): InspectionReport? = (_state.value as? FileInspectorUiState.Complete)?.report

    private fun launchInspection(request: FileInspectionRequest) {
        generation += 1L
        val operationGeneration = generation
        activeJob?.cancel()
        _state.value = FileInspectorUiState.Running(0L)
        activeJob = viewModelScope.launch {
            try {
                val report = withContext(Dispatchers.IO) {
                    val contentSource = FileInspectorContentSource(
                        getApplication<Application>().contentResolver,
                        request,
                    )
                    FileInspectionEngine().inspect(
                        source = object : InspectorSource {
                            override val declaredSize: Long = request.declaredSize

                            override fun openStream() = contentSource.openInputStream()
                        },
                        policy = InspectionPolicy(
                            maxBytes = FileInspectorIntentPolicy.MAX_DECLARED_SIZE,
                            progressEveryBytes = PROGRESS_INTERVAL_BYTES,
                        ),
                    ) { progress ->
                        if (operationGeneration == generation && !progress.isComplete) {
                            _state.value = FileInspectorUiState.Running(progress.bytesRead)
                        }
                    }
                }
                if (operationGeneration == generation) {
                    _state.value = FileInspectorUiState.Complete(report)
                }
            } catch (cancellation: CancellationException) {
                if (operationGeneration == generation) {
                    _state.value = FileInspectorUiState.Canceled
                }
                throw cancellation
            } catch (error: Exception) {
                if (operationGeneration == generation) {
                    _state.value = FileInspectorUiState.Failed(error)
                }
            }
        }
    }

    private companion object {
        const val PROGRESS_INTERVAL_BYTES = 8L * 1024L * 1024L
    }
}
