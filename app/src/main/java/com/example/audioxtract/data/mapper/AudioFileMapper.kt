package com.example.audioxtract.data.mapper

import com.example.audioxtract.domain.model.AudioFile
import java.io.File

object AudioFileMapper {

    fun mapToDomain(
        filePath: String,
        format: String
    ) = AudioFile(
        name = File(filePath).name,
        path = filePath,
        duration = 0L,   // Optional: we can add metadata later
        format = format
    )
}
