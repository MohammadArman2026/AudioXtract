package com.example.audioxtract.presentation.extractor

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun PlayScreen(uri: Uri?) {

    val context = LocalContext.current

    // Build and remember ExoPlayer
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            if (uri != null) {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                play()
            }
        }
    }

    // Release when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    // Track playback progress
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(player) {
        while (true) {
            duration = player.duration
            position = player.currentPosition
            isPlaying = player.isPlaying
            delay(200)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))


        // Progress Slider
        Slider(
            value = if (duration > 0) position.toFloat() / duration else 0f,
            onValueChange = {
                player.seekTo((it * duration).toLong())
            },
            modifier = Modifier.fillMaxWidth()
        )


        // Time labels (elapsed / total)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position))
            Text(formatTime(duration))
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Controls Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            // rewind 5 sec
            Button(onClick = {
                player.seekTo((position - 5000).coerceAtLeast(0))
            }) {
                Text("⏪ 5s")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // play / pause
            Button(onClick = {
                if (isPlaying) player.pause() else player.play()
            }) {
                Text(if (isPlaying) "⏸ Pause" else "▶ Play")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // forward 5 sec
            Button(onClick = {
                player.seekTo((position + 5000).coerceAtMost(duration))
            }) {
                Text("⏩ 5s")
            }
        }
    }
}

