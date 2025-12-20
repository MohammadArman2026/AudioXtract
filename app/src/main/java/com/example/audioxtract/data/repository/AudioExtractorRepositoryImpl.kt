package com.example.audioxtract.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.contextaware.ContextAware
import androidx.annotation.RequiresApi
import com.example.audioxtract.data.datasource.MediaExtractorDataSource
import com.example.audioxtract.data.mapper.AudioFileMapper
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.repository.AudioExtractorRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class AudioExtractorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSource: MediaExtractorDataSource
) : AudioExtractorRepository {

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun extractAudio(
        videoUri: Uri,
        outputFormat: String
    ): Result<AudioFile> = withContext(Dispatchers.IO) {

        try {
            val filename = "extracted_${System.currentTimeMillis()}.$outputFormat"
            val mime = if (outputFormat == "aac") "audio/aac" else "audio/mp4"

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mime)
            }

            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            val itemUri = context.contentResolver.insert(collection, values)
                ?: return@withContext Result.failure(Exception("Failed to create file in Downloads"))

            val success = dataSource.extractAudio(videoUri, itemUri)

            if (!success) {
                return@withContext Result.failure(Exception("Extraction failed"))
            }

            val audioFile = AudioFile(filename, itemUri.toString(), 0L, outputFormat)

            Result.success(audioFile)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

