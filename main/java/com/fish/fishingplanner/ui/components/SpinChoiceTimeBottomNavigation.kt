package com.fish.fishingplanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fish.fishingplanner.navigation.SpinChoiceTimeNavHost
import com.fish.fishingplanner.ui.theme.CosmicColors
import com.fish.fishingplanner.ui.theme.EggYellow
import com.fish.fishingplanner.viewmodel.TaskViewModel

@Composable
fun CosmicBottomNavigation(navController: NavHostController, viewModel: TaskViewModel) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val items = listOf(
        NavItem("Trips", Icons.Default.Home, "trips"),
        NavItem("Checklist", Icons.Default.List, "checklist"),
        NavItem("New Trip", Icons.Default.Create, "newTrip"),
        NavItem("Settings", Icons.Default.Settings, "settings")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(text = item.label)
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EggYellow,
                            selectedTextColor = EggYellow,
                            unselectedIconColor = CosmicColors.LightGray,
                            unselectedTextColor = CosmicColors.LightGray,
                            indicatorColor = CosmicColors.SpaceMiddle.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        SpinChoiceTimeNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)