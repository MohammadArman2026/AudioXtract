package com.example.audioxtract.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audioxtract.domain.model.AudioFile
import com.example.audioxtract.presentation.audio_list.AudioListScreen
import com.example.audioxtract.presentation.extractor.AudioPlayerScreen
import com.example.audioxtract.presentation.extractor.HomeScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoute.Home_Screen,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoute.Home_Screen) {

            HomeScreen(
                onPlay =
                    { audioFile ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("audio_file", audioFile)
                        navController.navigate(NavRoute.Audio_Player_Screen)
                    }
                ,
                onAudioListClick = {
                    navController.navigate(NavRoute.Audio_List_Screen)
                }
            )
        }

        composable(
            route = NavRoute.Audio_List_Screen
        ){
            AudioListScreen(
                onPlayClick = {audioFile->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("audio_file", audioFile)
                    navController.navigate(NavRoute.Audio_Player_Screen)
                }
            )
        }

        //audio player
        composable(
            route = NavRoute.Audio_Player_Screen,
        ){backStackEntry ->
            val audioFile = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<AudioFile>("audio_file")

            AudioPlayerScreen(
                audioFile = audioFile,
            )
        }
    }
}
