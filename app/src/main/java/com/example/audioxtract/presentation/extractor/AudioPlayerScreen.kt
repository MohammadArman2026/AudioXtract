package com.example.audioxtract.presentation.extractor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.audioxtract.R
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.presentation.components.ReusableText
import com.example.audioxtract.presentation.components.TopBar
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerScreen(audioFile: AudioFile?) {

    val context = LocalContext.current

    // Build and remember ExoPlayer
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioFile?.path?.toUri() ?:"".toUri()))
            prepare()
            play()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

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

    Scaffold (
        modifier = Modifier
        .fillMaxSize()
        .background(
                Brush.linearGradient(
                    listOf(Color(0xFFE8F4FF),
                        Color(0xFF0096FF))
                )
        ),
        topBar = {
            TopBar(title = "Now Playing...",
                fontSize = 16,
                fontWeight = FontWeight.Normal)
        }
    ){innerPadding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(64.dp))
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.music_audio),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(100.dp)

                )
            }
            Spacer(
                modifier = Modifier.height(64.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ){
                ReusableText(
                    text = audioFile?.name ?:"unknown",
                    fontsize = 16,
                    fontweight = FontWeight.SemiBold
                )
            }
            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Slider(
                value = if (duration > 0) position.toFloat() / duration else 0f,
                onValueChange = {
                    player.seekTo((it * duration).toLong())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(position))
                Text(formatTime(duration))
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.skip_previous),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable{
                            player.seekTo((position - 5000).coerceAtLeast(0))
                        }
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ){
                    val iconPainter = if (isPlaying) {
                        painterResource(id = R.drawable.pause) // drawable icon
                    } else {
                        rememberVectorPainter(Icons.Default.PlayArrow) // material icon
                    }

                    Icon(
                        painter = iconPainter,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable {
                                if (isPlaying) player.pause() else player.play()
                            }
                            .size(40.dp)
                    )

                }

                Icon(
                    painter = painterResource(R.drawable.skip_next ),
                        contentDescription = null,
                    modifier = Modifier
                        .clickable{
                            player.seekTo((position + 5000).coerceAtMost(duration))
                        }
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
    }
}


fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val min = totalSeconds / 60
    val sec = totalSeconds % 60
    return "%02d:%02d".format(min, sec)
}