package com.example.audioxtract.presentation.audio_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.domain.usecase.GetExtractedAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val getExtractedAudioUseCase: GetExtractedAudioUseCase
) : ViewModel(){
    private val _audioList = MutableStateFlow(AudioListUiState())
    val audioList = _audioList.asStateFlow()

    //when object of this class is created then init block will be called
    init{
        getAudioList()
    }
    fun getAudioList(){
        viewModelScope.launch {
            _audioList.value = AudioListUiState(loading = true)

            getExtractedAudioUseCase
                .invoke()
                .onSuccess {
                    _audioList.value = AudioListUiState(success = it)
                }
                .onFailure {
                    _audioList.value = AudioListUiState(error = it.message.toString())
                }
        }
    }
}

data class AudioListUiState(
    val loading :Boolean = false,
    val success :List<AudioFile> = emptyList(),
    val error :String = ""
)