package com.fish.fishingplanner.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fish.fishingplanner.NewTripScreen
import com.fish.fishingplanner.TripDetailsScreen
import com.fish.fishingplanner.ui.screens.home.CalendarScreen
import com.fish.fishingplanner.ui.screens.home.ChecklistScreen
import com.fish.fishingplanner.ui.screens.home.SettingsScreen
import com.fish.fishingplanner.ui.screens.home.TripsScreen

object TripDataManager {
    private val _trips = mutableListOf<Trip>()
    val trips: List<Trip> get() = _trips

    private var nextTripId = 1

    init {
        // Добавляем тестовые данные
        if (_trips.isEmpty()) {
            _trips.addAll(
                listOf(
                    Trip(
                        id = 1,
                        name = "Lake Weekend",
                        date = "2024-01-15",
                        location = "Lake Serene",
                        fishingType = "Boat",
                        status = "Planned",
                        targetFish = "Trout",
                        notes = "Weekend fishing trip with friends"
                    ),
                    Trip(
                        id = 2,
                        name = "Ice Fishing",
                        date = "2024-01-10",
                        location = "Frozen Lake",
                        fishingType = "Ice",
                        status = "Completed",
                        targetFish = "Perch",
                        notes = "Successful ice fishing session"
                    ),
                    Trip(
                        id = 3,
                        name = "Evening Shore",
                        date = "2024-01-20",
                        location = "River Bend",
                        fishingType = "Shore",
                        status = "Planned",
                        targetFish = "Carp",
                        notes = "Evening session"
                    )
                )
            )
            nextTripId = 4
        }
    }

    fun addTrip(trip: Trip): Int {
        val newTrip = trip.copy(id = nextTripId)
        _trips.add(newTrip)
        nextTripId++
        return newTrip.id
    }

    fun updateTrip(trip: Trip) {
        val index = _trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            _trips[index] = trip
        }
    }

    fun deleteTrip(tripId: Int) {
        _trips.removeAll { it.id == tripId }
    }

    fun getTrip(tripId: Int): Trip? {
        return _trips.find { it.id == tripId }
    }

    fun getStandardChecklistItems(): List<ChecklistItem> {
        return listOf(
            ChecklistItem(1, "Fishing Rod", "Equipment", false, true),
            ChecklistItem(2, "Reel", "Equipment", false, true),
            ChecklistItem(3, "Tackle Box", "Equipment", false, true),
            ChecklistItem(4, "Baits & Lures", "Equipment", false, true),
            ChecklistItem(5, "Fishing Line", "Equipment", false, true),
            ChecklistItem(6, "Rain Jacket", "Clothes", false, true),
            ChecklistItem(7, "Warm Clothes", "Clothes", false, true),
            ChecklistItem(8, "Boots", "Clothes", false, true),
            ChecklistItem(9, "Water Bottle", "Food & Drinks", false, true),
            ChecklistItem(10, "Snacks", "Food & Drinks", false, true),
            ChecklistItem(11, "First Aid Kit", "Other", false, true),
            ChecklistItem(12, "Fishing License", "Other", false, true),
            ChecklistItem(13, "Flashlight", "Other", false, true)
        )
    }
}

// Data classes
data class Trip(
    val id: Int,
    val name: String,
    val date: String,
    val location: String,
    val fishingType: String,
    val status: String, // "Planned" or "Completed"
    val targetFish: String = "",
    val notes: String = "",
    val checklist: List<ChecklistItem> = emptyList()
)

data class ChecklistItem(
    val id: Int,
    val name: String,
    val category: String,
    var isChecked: Boolean = false,
    val isStandard: Boolean = true
)

@Composable
fun SpinChoiceTimeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "trips",
        modifier = modifier
    ) {
        composable("trips") {
            TripsScreen(navController = navController)
        }
        composable("checklist") {
            ChecklistScreen(navController = navController)
        }
        composable("calendar") {
            CalendarScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("newTrip") {
            NewTripScreen(navController = navController)
        }
        composable("tripDetails/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull()
            TripDetailsScreen(navController = navController, tripId = tripId)
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Trips : Screen("trips", "Trips", Icons.Default.TripOrigin)
    object Checklist : Screen("checklist", "Checklist", Icons.Default.Checklist)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.DateRange)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}