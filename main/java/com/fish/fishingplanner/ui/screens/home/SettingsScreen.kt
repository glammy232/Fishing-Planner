package com.fish.fishingplanner.ui.screens.home

import android.R
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fish.fishingplanner.navigation.TripDataManager
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

data class Transaction(
    val id: String = Random.nextLong().toString(),
    val type: String, // "expense" или "income"
    val amount: Double,
    val category: String,
    val date: Date,
    val note: String = "",
    val source: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object TransactionStore {
    val transactions = mutableStateListOf<Transaction>()

    // Сумма всех расходов (expenses)
    val totalExpenses: Double
        get() = transactions
            .filter { it.type == "expense" }
            .sumOf { it.amount }

    // Сумма всех доходов (income)
    val totalIncome: Double
        get() = transactions
            .filter { it.type == "income" }
            .sumOf { it.amount }

    // Баланс (доходы - расходы)
    val balance: Double
        get() = totalIncome - totalExpenses

    // Транзакции по категориям
    val expensesByCategory: Map<String, Double>
        get() = transactions
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

    val incomeBySource: Map<String, Double>
        get() = transactions
            .filter { it.type == "income" }
            .groupBy {
                if (it.source.isNotEmpty()) it.source else it.category
            }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

    // Фильтрация по дате
    fun getTransactionsForDate(date: Date): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time

        return transactions.filter {
            it.date >= startOfDay && it.date < endOfDay
        }
    }

    fun getTransactionsForMonth(year: Int, month: Int): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.time

        return transactions.filter {
            it.date >= startOfMonth && it.date < endOfMonth
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

/*        // App Settings Section
        SectionHeader(title = "Application")

        SettingsItem(
            icon = Icons.Default.History,
            title = "Trip History",
            subtitle = "View completed fishing trips",
            onClick = {
                // Можно добавить отдельный экран или фильтр
            }
        )

        SettingsItem(
            icon = Icons.Default.ContentCopy,
            title = "Templates",
            subtitle = "Use trip templates",
            onClick = {
                // В будущем можно добавить
            }
        )

        SettingsItem(
            icon = Icons.Default.BarChart,
            title = "Statistics",
            subtitle = "View fishing stats",
            onClick = {
                showStatsDialog()
            }
        )

        SettingsItem(
            icon = Icons.Default.FileDownload,
            title = "Export Data",
            subtitle = "Export trips to CSV",
            onClick = {
                // В будущем можно добавить
            }
        )

        Spacer(modifier = Modifier.height(24.dp))*/

        // App Info Section
        SectionHeader(title = "Information")

        SettingsItem(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            onClick = {
                val privacyPolicyUrl = "https://fishiingpllanner.com/privacy-policy.html"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                LocalContext.current.startActivity(intent)
            }
        )

        SettingsItem(
            icon = Icons.Default.Info,
            title = "About",
            subtitle = "App version 1.0.0",
            onClick = {
                val privacyPolicyUrl = "https://fishiingpllanner.com"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                LocalContext.current.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Reset Section
        SectionHeader(title = "Danger Zone")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.Red
                    )

                    Text(
                        text = "Reset All Data",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }

                Text(
                    text = "This will delete all your trips. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                var showDialog by remember { mutableStateOf(false) }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Reset Data")
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirm Reset") },
                        text = { Text("Are you sure you want to delete all trips? This action cannot be undone.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    // В реальном приложении очищали бы список
                                    // TripDataManager.clearAllData()
                                    showDialog = false
                                }
                            ) {
                                Text("DELETE", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDialog = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // App Info Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fishing Trip Planner",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Version 1.0.0",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Made for anglers 🎣",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun showStatsDialog() {
    val trips = TripDataManager.trips

    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        val totalTrips = trips.size
        val completedTrips = trips.count { it.status == "Completed" }
        val plannedTrips = trips.count { it.status == "Planned" }

        // Calculate most common location
        val locationCounts = trips.groupingBy { it.location }.eachCount()
        val mostCommonLocation = locationCounts.maxByOrNull { it.value }?.key ?: "None"

        // Calculate favorite fishing type
        val typeCounts = trips.groupingBy { it.fishingType }.eachCount()
        val favoriteType = typeCounts.maxByOrNull { it.value }?.key ?: "None"

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Fishing Statistics") },
            text = {
                Column {
                    StatItem("Total Trips", totalTrips.toString())
                    StatItem("Completed Trips", completedTrips.toString())
                    StatItem("Planned Trips", plannedTrips.toString())
                    StatItem("Most Common Location", mostCommonLocation)
                    StatItem("Favorite Fishing Type", favoriteType)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun showAboutDialog() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("About Fishing Trip Planner") },
            text = {
                Text("Fishing Trip Planner helps you organize and prepare for your fishing trips. Plan your trips, manage your checklist, and keep track of your fishing history.")
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(){onClick},
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun CalendarScreen(navController: NavController) {
    val trips = TripDataManager.trips

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calendar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "View your fishing trips by date",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Simple calendar representation
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Trips Calendar",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Group trips by date
                    val tripsByDate = trips.groupBy { it.date }

                    if (tripsByDate.isEmpty()) {
                        Text(
                            text = "No trips scheduled",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        tripsByDate.forEach { (date, dateTrips) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = date,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    dateTrips.forEach { trip ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    navController.navigate("tripDetails/${trip.id}")
                                                },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(trip.name)
                                                Text(
                                                    text = "${trip.location} • ${trip.fishingType}",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
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
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}