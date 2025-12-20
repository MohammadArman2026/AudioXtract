package com.example.audioxtract.presentation.extractor



import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.usecase.ExtractAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtractorViewModel @Inject constructor(
    private val extractAudioUseCase: ExtractAudioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExtractorUiState>(ExtractorUiState.Idle)
    val uiState: StateFlow<ExtractorUiState> = _uiState

    fun extractAudio(videoUri: Uri, format: String) {
        viewModelScope.launch {
            _uiState.value = ExtractorUiState.Loading

            val result = extractAudioUseCase(videoUri, format)

            result.onSuccess {
                _uiState.value = ExtractorUiState.Success(it)
            }.onFailure {
                _uiState.value = ExtractorUiState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun reset() {
        _uiState.value = ExtractorUiState.Idle
    }
}

