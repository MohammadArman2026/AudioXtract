package com.example.audioxtract.presentation.extractor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExtractorScreen(
    viewModel: ExtractorViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFormat by remember { mutableStateOf("m4a") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedUri = uri
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {

        Button(onClick = { launcher.launch(arrayOf("video/*")) }) {
            Text("Pick Video")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedUri?.let {
            Text("Selected: $it")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { selectedFormat = "m4a" }) {
                Text("M4A")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { selectedFormat = "aac" }) {
                Text("AAC")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = selectedUri != null,
            onClick = {
                selectedUri?.let { uri ->
                    viewModel.extractAudio(uri, selectedFormat)
                }
            }
        ) {
            Text("Extract Audio")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState.value) {
            is ExtractorUiState.Idle -> {}
            is ExtractorUiState.Loading -> Text("Extracting...")
            is ExtractorUiState.Success -> Text("Saved: ${state.audioFile.path}")
            is ExtractorUiState.Error -> Text("Error: ${state.message}")
        }
    }
}

