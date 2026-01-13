package com.fish.fishingplanner.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import com.fish.fishingplanner.navigation.ChecklistItem
import com.fish.fishingplanner.navigation.TripDataManager

@Composable
fun ChecklistScreen(navController: NavController) {
    // Для стандартных элементов используем immutable список в remember
    val standardItems = remember {
        listOf(
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

    // Для кастомных элементов используем mutableStateOf со списком
    var customItems by remember { mutableStateOf<List<ChecklistItem>>(emptyList()) }
    var newItemText by remember { mutableStateOf("") }

    // Счетчик для новых ID кастомных элементов
    var nextCustomId by remember { mutableStateOf(100) }

    // Для обновления состояния стандартных элементов
    var updatedStandardItems by remember { mutableStateOf(standardItems) }

    // Объединяем все элементы
    val allItems = remember(updatedStandardItems, customItems) {
        (updatedStandardItems + customItems).sortedBy { it.category }
    }

    // Получаем уникальные категории
    val categories = remember(allItems) {
        allItems.map { it.category }.distinct().sorted()
    }

    // Coroutine scope для обновлений состояния
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Fishing Checklist",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Prepare everything for your fishing trip",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Progress indicator
            val checkedCount = allItems.count { it.isChecked }
            val totalCount = allItems.size

            Text(
                text = "$checkedCount/$totalCount items checked",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            LinearProgressIndicator(
                progress = if (totalCount > 0) checkedCount.toFloat() / totalCount else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Checklist items
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Показываем элементы по категориям
            categories.forEach { category ->
                item {
                    Text(
                        text = category.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Фильтруем элементы по категории
                val categoryItems = allItems.filter { it.category == category }

                // Отображаем элементы категории
                items(categoryItems, key = { it.id }) { item ->
                    // Используем derivedStateOf для реактивности чекбокса
                    val isChecked by remember(item.id) {
                        derivedStateOf {
                            if (item.isStandard) {
                                updatedStandardItems.find { it.id == item.id }?.isChecked ?: false
                            } else {
                                customItems.find { it.id == item.id }?.isChecked ?: false
                            }
                        }
                    }

                    ChecklistItemRow(
                        item = item.copy(isChecked = isChecked),
                        onCheckedChange = { checked ->
                            scope.launch {
                                if (item.isStandard) {
                                    // Обновляем стандартные элементы
                                    updatedStandardItems = updatedStandardItems.map {
                                        if (it.id == item.id) it.copy(isChecked = checked) else it
                                    }
                                } else {
                                    // Обновляем кастомные элементы
                                    customItems = customItems.map {
                                        if (it.id == item.id) it.copy(isChecked = checked) else it
                                    }
                                }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                if (!item.isStandard) {
                                    customItems = customItems.filter { it.id != item.id }
                                }
                            }
                        }
                    )
                }
            }

            item {
                // Add new item section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Add Custom Item",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newItemText,
                                onValueChange = { newItemText = it },
                                label = { Text("Item name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("Enter item name...") },
                                // ИСПРАВЛЕНО: Указываем явно цвет текста
                                textStyle = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedPlaceholderColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.LightGray
                                )
                            )

                            FilledTonalButton(
                                onClick = {
                                    if (newItemText.isNotBlank()) {
                                        // Создаем новый кастомный элемент
                                        val newItem = ChecklistItem(
                                            id = nextCustomId,
                                            name = newItemText.trim(),
                                            category = "Custom",
                                            isStandard = false
                                        )

                                        // Добавляем в список
                                        customItems = customItems + newItem

                                        // Увеличиваем счетчик ID
                                        nextCustomId += 1

                                        // Очищаем поле ввода
                                        newItemText = ""
                                    }
                                },
                                enabled = newItemText.isNotBlank(),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        }

                        // Подсказка
                        if (newItemText.isBlank()) {
                            Text(
                                text = "Enter item name and press Add button",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                    color = if (item.isChecked) Color.Gray else Color.Black,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }

            if (!item.isStandard) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}