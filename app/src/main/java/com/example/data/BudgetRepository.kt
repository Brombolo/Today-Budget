package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BudgetRepository(private val budgetDao: BudgetDao) {

    // Streams
    val categoriesFlow: Flow<List<Category>> = budgetDao.getAllCategoriesFlow()
    val expensesFlow: Flow<List<Expense>> = budgetDao.getAllExpensesFlow()
    val adjustmentsFlow: Flow<List<Adjustment>> = budgetDao.getAllAdjustmentsFlow()

    // Sync state
    suspend fun getAllCategories(): List<Category> = budgetDao.getAllCategories()
    suspend fun getAllExpenses(): List<Expense> = budgetDao.getAllExpenses()
    suspend fun getAllAdjustments(): List<Adjustment> = budgetDao.getAllAdjustments()

    // Ensure Default Categories
    suspend fun ensureDefaultCategories() {
        val existing = budgetDao.getAllCategories()
        if (existing.isEmpty()) {
            budgetDao.insertCategory(Category(name = "Cibo", icon = "🍔", isDefault = true))
            budgetDao.insertCategory(Category(name = "Casa", icon = "🏠", isDefault = true))
            budgetDao.insertCategory(Category(name = "Trasporti", icon = "🚗", isDefault = true))
            budgetDao.insertCategory(Category(name = "Tabacchi", icon = "🚬", isDefault = true))
        }
    }

    // Category CRUD
    suspend fun getCategoryById(id: Int): Category? = budgetDao.getCategoryById(id)
    suspend fun insertCategory(category: Category) = budgetDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = budgetDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = budgetDao.deleteCategory(category)

    // Expense CRUD
    suspend fun insertExpense(expense: Expense) = budgetDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = budgetDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = budgetDao.deleteExpense(expense)

    // Adjustment CRUD
    suspend fun insertAdjustment(adjustment: Adjustment) = budgetDao.insertAdjustment(adjustment)
    suspend fun updateAdjustment(adjustment: Adjustment) = budgetDao.updateAdjustment(adjustment)
    suspend fun deleteAdjustment(adjustment: Adjustment) = budgetDao.deleteAdjustment(adjustment)

    // Settings
    suspend fun getSetting(key: String, defaultValue: String): String {
        return budgetDao.getSettingValue(key) ?: defaultValue
    }

    suspend fun saveSetting(key: String, value: String) {
        budgetDao.insertSetting(Setting(key, value))
    }
}
