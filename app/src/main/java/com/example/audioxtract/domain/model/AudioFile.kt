package com.example.audioxtract.domain.model

data class AudioFile(
    val name: String,
    val path:String,
    val duration: Long,
    val format: String
)
