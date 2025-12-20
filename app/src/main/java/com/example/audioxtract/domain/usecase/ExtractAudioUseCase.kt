package com.example.audioxtract.domain.usecase

import android.net.Uri
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.repository.AudioExtractorRepository
import javax.inject.Inject

class ExtractAudioUseCase @Inject constructor(
    private val repository: AudioExtractorRepository
) {
    suspend operator fun invoke(videoUri: Uri, format: String) =
        repository.extractAudio(videoUri, format)
}
