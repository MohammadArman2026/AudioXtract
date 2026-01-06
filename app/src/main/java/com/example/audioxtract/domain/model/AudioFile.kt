package com.example.audioxtract.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val name: String,
    val path:String,
    val duration: Long,
    val format: String
):Parcelable
