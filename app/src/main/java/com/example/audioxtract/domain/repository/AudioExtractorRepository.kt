package com.example.audioxtract.domain.repository

import android.net.Uri
import com.example.audioxtract.domain.model.AudioFile

interface AudioExtractorRepository {
    suspend fun extractAudio(videoUri: Uri, outputFormat: String): kotlin.Result<AudioFile>
    suspend fun getExtractedAudio():kotlin.Result<List<AudioFile>>
}


// this is interface which is smple a contract of what a class will do like
//this interface has function of extracting audio so it will simply extract audio
//and will return the audio file
//in the function extract audio it will take video uri and output format and then
//will convert in the output format
