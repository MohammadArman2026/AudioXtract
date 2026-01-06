package com.example.audioxtract.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.audioxtract.data.datasource.MediaExtractorDataSource
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.repository.AudioExtractorRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioExtractorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSource: MediaExtractorDataSource
) : AudioExtractorRepository {

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun extractAudio(
        videoUri: Uri,
        outputFormat: String
    ): kotlin.Result<AudioFile> = withContext(Dispatchers.IO) {

        try {
            val originalName = getFileNameFromUri(videoUri)
            val baseName = originalName.substringBeforeLast(".")
            val filename = "extracted_${baseName}.$outputFormat"

            val mime = if (outputFormat == "aac") "audio/aac" else "audio/mp4"

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mime)
            }

            val collection =
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            val itemUri = context.contentResolver.insert(collection, values)
                ?: return@withContext Result.failure(
                    Exception("Failed to create audio file")
                )

            val success = dataSource.extractAudio(videoUri, itemUri)

            if (!success) {
                return@withContext Result.failure(Exception("Extraction failed"))
            }

            val audioFile = AudioFile(
                name = filename,
                path = itemUri.toString(),
                duration = 0L,
                format = outputFormat
            )

            Result.success(audioFile)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun getExtractedAudio(): Result<List<AudioFile>> =
        withContext(Dispatchers.IO) {

            try {
                val audioList = mutableListOf<AudioFile>()

                val collection =
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val projection = arrayOf(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.MIME_TYPE
                )

                val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
                val selectionArgs = arrayOf("extracted_%")

                val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->

                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        val filename = cursor.getString(nameCol)
                        val id = cursor.getLong(idCol)
                        val size = cursor.getLong(sizeCol)
                        val mime = cursor.getString(mimeCol)

                        val uri =
                            Uri.withAppendedPath(collection, id.toString())

                        audioList.add(
                            AudioFile(
                                name = filename,
                                path = uri.toString(),
                                duration = size,
                                format = if (mime.contains("aac")) "aac" else "m4a"
                            )
                        )
                    }
                }

                Result.success(audioList)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getFileNameFromUri(uri: Uri): String {
        var name = "video"

        context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                name = cursor.getString(0)
            }
        }

        return name
    }
}

