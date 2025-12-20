package com.example.audioxtract.presentation.extractor

import com.example.audioxtract.domain.model.AudioFile

sealed class ExtractorUiState {
    object Idle : ExtractorUiState()
    object Loading : ExtractorUiState()
    data class Success(val audioFile: AudioFile) : ExtractorUiState()
    data class Error(val message: String) : ExtractorUiState()
}
