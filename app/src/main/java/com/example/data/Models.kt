package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String, // Emoji or icon name (e.g., "🍔", "🏠")
    val isDefault: Boolean = false
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val categoryId: Int,
    val timestamp: Long, // Epoch millisecond based
    val description: String = ""
)

@Entity(tableName = "adjustments")
data class Adjustment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double, // + positive for income, - negative for correction
    val timestamp: Long,
    val note: String = ""
)

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "monthly_budgets")
data class MonthlyBudget(
    @PrimaryKey val cycleStartDate: String, // format "YYYY-MM-DD"
    val budget: Double
)
