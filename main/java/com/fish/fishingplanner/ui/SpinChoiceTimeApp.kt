package com.fish.fishingplanner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fish.fishingplanner.navigation.SpinChoiceTimeNavHost
import com.fish.fishingplanner.ui.components.CosmicBottomNavigation
import com.fish.fishingplanner.ui.theme.BubbleOrbitTheme
import com.fish.fishingplanner.viewmodel.TaskViewModel

@Composable
fun SpinChoiceTimeApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()

    BubbleOrbitTheme {
        // Временный вариант без bottom navigation - сначала проверим основную навигацию
        /*BubbleOrbitNavHost(
            navController = navController,
            modifier = Modifier,
            viewModel = viewModel
        )*/


        Column(modifier = Modifier.fillMaxSize()) {
            SpinChoiceTimeNavHost(
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            CosmicBottomNavigation(
                navController = navController,
                viewModel
            )
        }
    }
}