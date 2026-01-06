package com.example.audioxtract.presentation.extractor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.net.toUri
import com.example.audioxtract.domain.model.AudioFile


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorScreen(
    viewModel: ExtractorViewModel = hiltViewModel(),
    onPlay: (Uri) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFormat by remember { mutableStateOf("m4a") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedUri = uri
    }

    val buttonText = if (selectedUri == null) "Pick Video" else "Extract"


    LaunchedEffect(uiState) {
        when (uiState) {
            is ExtractorUiState.Success -> {
                Toast.makeText(context, "Audio extracted!", Toast.LENGTH_SHORT).show()
                selectedUri = null
                viewModel.reset()
            }

            is ExtractorUiState.Error -> {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                viewModel.reset()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AudioXtract",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (uiState is ExtractorUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Button(
                onClick = {
                    if (selectedUri == null) {
                        videoPickerLauncher.launch(arrayOf("video/*"))
                    } else {
                        viewModel.extractAudio(selectedUri!!, selectedFormat)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                enabled = uiState !is ExtractorUiState.Loading // disables while processing
            ) {
                Text(buttonText)
            }
        }
    }
}


//@Composable
//fun AudioListScreen(
//    viewModel: ExtractorViewModel,
//    onPlay: (Uri) -> Unit
//) {
//    val audioList by viewModel.audioList.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.getExtractedAudio()
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .padding(top= 4.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.spacedBy(4.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        items(audioList.size) { index ->
//            AudioCard(
//                audio = audioList[index],
//                onPlay = onPlay
//            )
//        }
//    }
//}

//@Composable
//fun AudioCard(audio: AudioFile,
//              onPlay: (Uri) -> Unit){
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start =8.dp ,end =8.dp)
//            .height(50.dp)
//            .border(
//                width = 1.dp,
//                shape = RoundedCornerShape(8.dp),
//                color = MaterialTheme.colorScheme.primary
//            ),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ){
//      Text(
//          text =audio.name,
//          modifier = Modifier.padding(start = 4.dp)
//      )
//
//        Icon(
//            imageVector = Icons.Default.PlayArrow,
//            modifier = Modifier
//                .clickable{
//                    onPlay(
//                        audio.path.toUri()
//                    )
//                }
//                .padding(end = 16.dp),
//            contentDescription = null,
//        )
//    }
//}
