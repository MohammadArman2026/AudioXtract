package com.example.audioxtract.presentation.extractor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.presentation.components.AudioCard
import com.example.audioxtract.presentation.components.PlayGradientCircle
import com.example.audioxtract.presentation.components.ReusableText
import com.example.audioxtract.presentation.components.SelectedVideoCard
import com.example.audioxtract.presentation.components.TopBar
import com.example.audioxtract.presentation.components.VideoPickerButton

@Composable
fun HomeScreen(
    onPlay:(AudioFile) -> Unit,
    onAudioListClick: () -> Unit
) {

    val viewModel: ExtractorViewModel = hiltViewModel()
    val latestAudioFile by viewModel.latestAudioFile.collectAsState()
    val uriData by viewModel.uriData.collectAsState()
    val selectedUri by  viewModel.selectedVideoUri
    var selectedFormat by remember { mutableStateOf("m4a") }
    val uiState by viewModel.uiState.collectAsState()

  val context = LocalContext.current
    val videoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.getUriData(
                context = context,
                videoUri = it
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopBar(title = "Audio Extractor")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            VideoPickerCard{
                videoPickerLauncher.launch(
                    arrayOf("video/*")
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if(selectedUri!= null){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Selected Video",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable{
                            viewModel.clearSelection()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                SelectedVideoCard(
                     uriData = uriData,
                    onExtractClick = {
                        viewModel.extractAudio(selectedUri?: Uri.EMPTY, selectedFormat)
                    },
                    isLoading = uiState is ExtractorUiState.Loading
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if(latestAudioFile != null){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Extracted Video",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "See all",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .clickable{
                            onAudioListClick()
                        }
                    )
                }
                // Spacer(modifier = Modifier.height(16.dp))
                AudioCard(latestAudioFile
                ) { onPlay(latestAudioFile!!) }
            }
        }
    }
}

@Composable
fun VideoPickerCard(onPickClick:()-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PlayGradientCircle()
            }
            ReusableText(text = "Convert Video")
            ReusableText(
                text = "Select a video from gallery to extract audio instantly",
                fontsize = 16,
                fontweight = FontWeight.Normal
            )
            VideoPickerButton(
                onPickClick = {
                    onPickClick()
                },
                text = "Pick Video"
            )
        }
    }
}




