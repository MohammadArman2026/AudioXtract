package com.example.audioxtract.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.audioxtract.navigation.BottomNavRoutes

@Composable
fun BottomBar(navController: NavController,
              currentRoute:String?){
    val tabs =listOf(
        BottomNavRoutes.Home,
        BottomNavRoutes.List
    )

//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        tabs.forEach { tab->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    tab.title
                }
            )
        }
    }
}