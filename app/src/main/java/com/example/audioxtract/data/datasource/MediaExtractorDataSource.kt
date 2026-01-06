package com.example.audioxtract.data.datasource

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import javax.inject.Inject
class MediaExtractorDataSource @Inject constructor(
    private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun extractAudio(inputUri: Uri, outputUri: Uri): Boolean {

        val resolver = context.contentResolver
        val extractor = MediaExtractor()

        val input = resolver.openFileDescriptor(inputUri, "r") ?: return false
        extractor.setDataSource(input.fileDescriptor)

        val output = resolver.openFileDescriptor(outputUri, "rw") ?: return false
        val muxer = MediaMuxer(output.fileDescriptor, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        // EXACT SAME extraction loop as before
        var audioTrackIndex = -1
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("audio/")) {
                audioTrackIndex = i
                break
            }
        }
        if (audioTrackIndex == -1) {
            extractor.release()
            muxer.release()
            return false
        }

        extractor.selectTrack(audioTrackIndex)

        val format = extractor.getTrackFormat(audioTrackIndex)
        val newTrackIndex = muxer.addTrack(format)

        muxer.start()

        val bufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val buffer = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()

        while (true) {
            val sampleSize = extractor.readSampleData(buffer, 0)
            if (sampleSize < 0) break

            bufferInfo.apply {
                offset = 0
                size = sampleSize
                flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                presentationTimeUs = extractor.sampleTime
            }

            muxer.writeSampleData(newTrackIndex, buffer, bufferInfo)

            extractor.advance()
        }

        muxer.stop()
        muxer.release()
        extractor.release()

        return true
    }
}
