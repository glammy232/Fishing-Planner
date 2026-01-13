package com.fish.fishingplanner

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.fish.fishingplanner.ui.SpinChoiceTimeApp
import com.fish.fishingplanner.ui.components.CosmicBottomNavigation
import com.fish.fishingplanner.ui.theme.ChickenTheme
import com.fish.fishingplanner.viewmodel.TaskViewModel
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fish.fishingplanner.navigation.Trip
import com.fish.fishingplanner.navigation.TripDataManager

import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppKeyHash("com.fish.fishingplanner")
        getAppKeyHashHex("com.fish.fishingplanner")

        getAppKeyHash256("com.fish.fishingplanner")
        getAppKeyHashHex256("com.fish.fishingplanner")
        // Enable edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            //ChickenApp(viewModel = viewModel)
            SpinChoiceTimeApp(viewModel)
        }
    }
    fun getAppKeyHash(pack: String) {
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                pack,
                PackageManager.GET_SIGNATURES
            )

            for (signature in packageInfo.signatures!!) {
                val md = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KEY_HASH", "Key Hash: $keyHash")
                // Выведет в Logcat что-то вроде: "abc123def456..."
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAppKeyHashHex(pack: String) {
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                pack,
                PackageManager.GET_SIGNATURES
            )

            for (signature in packageInfo.signatures!!) {
                val md = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                val digest = md.digest()

                // HEX формат (как в signingReport)
                val hexString = StringBuilder()
                for (i in digest.indices) {
                    if (i > 0) hexString.append(":")
                    val hex = Integer.toHexString(0xff and digest[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                Log.d("KEY_HASH", "Key Hash (HEX): ${hexString}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAppKeyHash256(pack: String) {
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                pack,
                PackageManager.GET_SIGNATURES
            )

            for (signature in packageInfo.signatures!!) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KEY_HASH256", "Key 256: $keyHash")
                // Выведет в Logcat что-то вроде: "abc123def456..."
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAppKeyHashHex256(pack: String) {
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                pack,
                PackageManager.GET_SIGNATURES
            )

            for (signature in packageInfo.signatures!!) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val digest = md.digest()

                // HEX формат (как в signingReport)
                val hexString = StringBuilder()
                for (i in digest.indices) {
                    if (i > 0) hexString.append(":")
                    val hex = Integer.toHexString(0xff and digest[i].toInt())
                    if (hex.length == 1) hexString.append('0')
                    hexString.append(hex)
                }
                Log.d("KEY_HASH256", "Key Hash (HEX)256: ${hexString}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
@Composable
fun ChickenApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    //val currentTheme by viewModel.theme.collectAsState()

    ChickenTheme(darkTheme = true) {
        CosmicBottomNavigation(
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun NewTripScreen(navController: NavController) {
    var tripName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var fishingType by remember { mutableStateOf("") }
    var targetFish by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "New Trip",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(48.dp)) // For balance
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = tripName,
                onValueChange = { tripName = it },
                label = { Text("Trip Name*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD)*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fishingType,
                onValueChange = { fishingType = it },
                label = { Text("Fishing Type (Ice/Shore/Boat)*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetFish,
                onValueChange = { targetFish = it },
                label = { Text("Target Fish (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (tripName.isNotBlank() && date.isNotBlank() &&
                        location.isNotBlank() && fishingType.isNotBlank()) {

                        val newTrip = Trip(
                            id = 0, // Will be set by manager
                            name = tripName,
                            date = date,
                            location = location,
                            fishingType = fishingType,
                            status = "Planned",
                            targetFish = targetFish,
                            notes = notes
                        )

                        TripDataManager.addTrip(newTrip)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tripName.isNotBlank() && date.isNotBlank() &&
                        location.isNotBlank() && fishingType.isNotBlank()
            ) {
                Text("Save Trip")
            }
        }
    }
}

@Composable
fun TripDetailsScreen(
    navController: NavController,
    tripId: Int?
) {
    val trip = remember(tripId) { tripId?.let { TripDataManager.getTrip(it) } }

    if (trip == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Trip not found")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Trip Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    // В будущем можно добавить редактирование
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }

        // Trip Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = trip.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TripDetailItem(label = "Date", value = trip.date)
                    TripDetailItem(label = "Location", value = trip.location)
                    TripDetailItem(label = "Fishing Type", value = trip.fishingType)

                    if (trip.targetFish.isNotEmpty()) {
                        TripDetailItem(label = "Target Fish", value = trip.targetFish)
                    }

                    if (trip.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Notes:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(trip.notes)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(
                            containerColor = if (trip.status == "Completed")
                                Color(0xFF4CAF50)
                            else
                                MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Text(trip.status)
                        }
                    }
                }
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (trip.status == "Planned") {
                    Button(
                        onClick = {
                            val updatedTrip = trip.copy(status = "Completed")
                            TripDataManager.updateTrip(updatedTrip)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Completed")
                    }
                }

                Button(
                    onClick = {
                        TripDataManager.deleteTrip(trip.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete Trip")
                }
            }
        }
    }
}

@Composable
fun TripDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        Text(value)
    }
}