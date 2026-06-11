package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpensesFlow(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    fun getExpensesBetweenFlow(start: Long, end: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    suspend fun getExpensesBetween(start: Long, end: Long): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- ADJUSTMENTS ---
    @Query("SELECT * FROM adjustments ORDER BY timestamp DESC")
    fun getAllAdjustmentsFlow(): Flow<List<Adjustment>>

    @Query("SELECT * FROM adjustments ORDER BY timestamp DESC")
    suspend fun getAllAdjustments(): List<Adjustment>

    @Query("SELECT * FROM adjustments WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    suspend fun getAdjustmentsBetween(start: Long, end: Long): List<Adjustment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdjustment(adjustment: Adjustment): Long

    @Update
    suspend fun updateAdjustment(adjustment: Adjustment)

    @Delete
    suspend fun deleteAdjustment(adjustment: Adjustment)

    // --- SETTINGS ---
    @Query("SELECT value FROM settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: Setting)
}
