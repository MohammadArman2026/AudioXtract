package com.example.audioxtract.domain.usecase

import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.repository.AudioExtractorRepository
import javax.inject.Inject

class GetExtractedAudioUseCase @Inject constructor( private val repository: AudioExtractorRepository) {
    suspend operator fun invoke() :kotlin.Result<List<AudioFile>> {
        return repository.getExtractedAudio()
    }
}