package com.fish.fishingplanner.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Wheel(
    val id: String,
    var name: String,
    val category: String,
    val themeColor: Color,
    val options: MutableList<String> = mutableListOf(),
    val isAutoCreated: Boolean = false
)

object WheelManager {
    private val wheels = mutableListOf<Wheel>()
    private var currentId = 0

    // Доступные цвета для тем
    private val themeColors = listOf(
        Color(0xFF9D4EDD),
        Color(0xFF4CC9F0),
        Color(0xFFFFD166),
        Color(0xFF00D4FF),
        Color(0xFFFF6B8B)
    )

    init {
        // Для теста создадим пару колёс при запуске
        createWheel("What to do today", "Daily", themeColors[0])
        createWheel("Dinner ideas", "Food", themeColors[1])
    }

    fun createWheel(name: String, category: String, themeColor: Color? = null, isAutoCreated: Boolean = false): Wheel {
        currentId++
        val wheel = Wheel(
            id = "wheel_$currentId",
            name = name,
            category = category,
            themeColor = themeColor ?: themeColors[Random.nextInt(themeColors.size)],
            isAutoCreated = isAutoCreated
        )
        wheels.add(wheel)
        println("Wheel created: ${wheel.id} - ${wheel.name}") // Для отладки
        return wheel
    }

    fun createAutoWheel(): Wheel {
        val wheelCount = wheels.size + 1
        return createWheel(
            name = "Wheel $wheelCount",
            category = "Custom",
            isAutoCreated = true
        )
    }

    fun addOptionsToWheel(wheelId: String, options: List<String>) {
        val wheel = wheels.find { it.id == wheelId }
        if (wheel != null) {
            wheel.options.addAll(options)
            println("Added ${options.size} options to wheel ${wheel.id}") // Для отладки
        }
    }

    fun getAllWheels(): List<Wheel> {
        return wheels.toList()
    }

    fun getWheelById(id: String): Wheel? {
        return wheels.find { it.id == id }
    }

    fun deleteWheel(id: String) {
        wheels.removeIf { it.id == id }
        println("Wheel deleted: $id") // Для отладки
    }

    fun updateWheelName(id: String, newName: String) {
        val wheel = wheels.find { it.id == id }
        wheel?.name = newName
    }

    fun getRandomWheelWithOptions(): Wheel? {
        val wheelsWithOptions = wheels.filter { it.options.isNotEmpty() }
        return if (wheelsWithOptions.isNotEmpty()) {
            wheelsWithOptions.random()
        } else {
            wheels.firstOrNull()
        }
    }
}