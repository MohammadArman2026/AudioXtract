package com.example.audioxtract.presentation.extractor



import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.usecase.ExtractAudioUseCase
import com.example.audioxtract.domain.usecase.GetExtractedAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class uriData(
    val thumbnail :Bitmap ? =null,
    val fileName :String ="unknown",
    val duration :Long =0L
)
@HiltViewModel
class ExtractorViewModel @Inject constructor(
    private val extractAudioUseCase: ExtractAudioUseCase,
) : ViewModel() {
    private val _latestAudioFile = MutableStateFlow<AudioFile?>(null)
    val latestAudioFile: StateFlow<AudioFile?> = _latestAudioFile

    private val _uiState = MutableStateFlow<ExtractorUiState>(ExtractorUiState.Idle)
    val uiState: StateFlow<ExtractorUiState> = _uiState

    private val _uriState = MutableStateFlow<uriData?>(null)
    val uriData = _uriState.asStateFlow()

    private val _selectedVideoUri = mutableStateOf<Uri?>(null)
    val selectedVideoUri = _selectedVideoUri


    fun clearSelection() {
        _selectedVideoUri.value = null
    }

    fun extractAudio(videoUri: Uri, format: String) {
        viewModelScope.launch {
            _uiState.value = ExtractorUiState.Loading

            val result = extractAudioUseCase(videoUri, format)
            delay(1000)
            result.onSuccess {
                _uiState.value = ExtractorUiState.Success(it)
                _latestAudioFile.value = it
            }.onFailure {
                _uiState.value = ExtractorUiState.Error(it.message ?: "Unknown error")
            }
            _selectedVideoUri.value = null
        }
    }

    fun reset() {
        _uiState.value = ExtractorUiState.Idle
    }

    fun getUriData(context: Context,
                     videoUri: Uri){
        viewModelScope.launch {
            _selectedVideoUri.value = videoUri
            val thumbnail = getVideoThumbnail(context, videoUri)
            val fileName = getFileNameFromUri(context, videoUri)
            val duration = getVideoDuration(context, videoUri)
            val uriData = uriData(thumbnail, fileName, duration)
            _uriState.value= uriData
        }
    }
}



suspend fun getVideoThumbnail(
    context: Context,
    videoUri: Uri
): Bitmap? = withContext(Dispatchers.IO) {

    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, videoUri)
        retriever.getFrameAtTime(
            1_000_000,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}

suspend fun getFileNameFromUri(
    context: Context,
    uri: Uri
): String {
    var fileName = "unknown"

    context.contentResolver.query(
        uri,
        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    MediaStore.MediaColumns.DISPLAY_NAME
                )
            )
        }
    }

    return fileName
}


fun getVideoDuration(
    context: Context,
    videoUri: Uri
): Long {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION
        )?.toLong() ?: 0L
    } catch (e: Exception) {
        0L
    } finally {
        retriever.release()
    }
}

