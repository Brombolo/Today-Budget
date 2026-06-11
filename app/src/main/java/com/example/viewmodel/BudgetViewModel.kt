package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NotificationScheduler
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(db.budgetDao())

    // Base flows
    val categories = repository.categoriesFlow
    val expenses = repository.expensesFlow
    val adjustments = repository.adjustmentsFlow

    // Settings States to trigger recalculations reactively
    private val _monthlyBudget = MutableStateFlow(1000.0)
    val monthlyBudget = _monthlyBudget.asStateFlow()

    private val _billingStartDay = MutableStateFlow(1)
    val billingStartDay = _billingStartDay.asStateFlow()

    private val _dayStartHour = MutableStateFlow(0)
    val dayStartHour = _dayStartHour.asStateFlow()

    private val _carryOverEnabled = MutableStateFlow(false)
    val carryOverEnabled = _carryOverEnabled.asStateFlow()

    private val _currencySymbol = MutableStateFlow("€")
    val currencySymbol = _currencySymbol.asStateFlow()

    private val _pushDailyEnabled = MutableStateFlow(false)
    val pushDailyEnabled = _pushDailyEnabled.asStateFlow()

    private val _pushDailyTime = MutableStateFlow("20:00")
    val pushDailyTime = _pushDailyTime.asStateFlow()

    private val _pushWeeklyMonthlyEnabled = MutableStateFlow(false)
    val pushWeeklyMonthlyEnabled = _pushWeeklyMonthlyEnabled.asStateFlow()

    private val _pushBudgetConfirmEnabled = MutableStateFlow(false)
    val pushBudgetConfirmEnabled = _pushBudgetConfirmEnabled.asStateFlow()

    private val _userName = MutableStateFlow("Marco")
    val userName = _userName.asStateFlow()

    // Main combined UI State for budget calculations
    val budgetState: StateFlow<BudgetState> = combine(
        expenses,
        adjustments,
        monthlyBudget,
        billingStartDay,
        dayStartHour,
        carryOverEnabled
    ) { arr ->
        val flowExpenses = arr[0] as List<Expense>
        val flowAdjustments = arr[1] as List<Adjustment>
        val defaultBudget = arr[2] as Double
        val startDay = arr[3] as Int
        val startHour = arr[4] as Int
        val carryOver = arr[5] as Boolean
        calculateBudgetState(flowExpenses, flowAdjustments, defaultBudget, startDay, startHour, carryOver)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetState()
    )

    init {
        viewModelScope.launch {
            repository.ensureDefaultCategories()

            // Load initial settings synchronously of viewmodel starting
            _monthlyBudget.value = repository.getSetting("monthly_budget", "1000.0").toDoubleOrNull() ?: 1000.0
            _billingStartDay.value = repository.getSetting("billing_start_day", "1").toIntOrNull() ?: 1
            _dayStartHour.value = repository.getSetting("day_start_hour", "0").toIntOrNull() ?: 0
            _carryOverEnabled.value = repository.getSetting("carry_over_enabled", "false").toBoolean()

            _currencySymbol.value = repository.getSetting("currency_symbol", "€")
            _pushDailyEnabled.value = repository.getSetting("push_daily_enabled", "false").toBoolean()
            _pushDailyTime.value = repository.getSetting("push_daily_time", "20:00")
            _pushWeeklyMonthlyEnabled.value = repository.getSetting("push_weekly_monthly_enabled", "false").toBoolean()
            _pushBudgetConfirmEnabled.value = repository.getSetting("push_budget_confirm_enabled", "false").toBoolean()
            _userName.value = repository.getSetting("user_name", "Marco")
        }
    }

    // Settings Updates
    fun updateSettings(budget: Double, startDay: Int, startHour: Int, carryOver: Boolean, userNameStr: String) {
        viewModelScope.launch {
            repository.saveSetting("monthly_budget", budget.toString())
            repository.saveSetting("billing_start_day", startDay.toString())
            repository.saveSetting("day_start_hour", startHour.toString())
            repository.saveSetting("carry_over_enabled", carryOver.toString())
            repository.saveSetting("user_name", userNameStr)

            // Sync flow properties reactively
            _monthlyBudget.value = budget
            _billingStartDay.value = startDay
            _dayStartHour.value = startHour
            _carryOverEnabled.value = carryOver
            _userName.value = userNameStr
        }
    }

    fun updateNotificationAndCurrencySettings(
        currency: String,
        pushDaily: Boolean,
        pushDailyTimeVal: String,
        pushWeeklyMonthly: Boolean,
        pushBudgetConfirm: Boolean
    ) {
        viewModelScope.launch {
            repository.saveSetting("currency_symbol", currency)
            repository.saveSetting("push_daily_enabled", pushDaily.toString())
            repository.saveSetting("push_daily_time", pushDailyTimeVal)
            repository.saveSetting("push_weekly_monthly_enabled", pushWeeklyMonthly.toString())
            repository.saveSetting("push_budget_confirm_enabled", pushBudgetConfirm.toString())

            _currencySymbol.value = currency
            _pushDailyEnabled.value = pushDaily
            _pushDailyTime.value = pushDailyTimeVal
            _pushWeeklyMonthlyEnabled.value = pushWeeklyMonthly
            _pushBudgetConfirmEnabled.value = pushBudgetConfirm

            // Schedule the alarm daily
            NotificationScheduler.scheduleDailyNotification(getApplication(), pushDaily, pushDailyTimeVal)
        }
    }

    // Expense Logic
    fun addExpense(amount: Double, categoryId: Int, description: String = "", timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.insertExpense(Expense(amount = amount, categoryId = categoryId, description = description, timestamp = timestamp))
        }
    }

    fun updateExpense(id: Int, amount: Double, categoryId: Int, description: String, timestamp: Long) {
        viewModelScope.launch {
            repository.updateExpense(Expense(id = id, amount = amount, categoryId = categoryId, description = description, timestamp = timestamp))
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Categories Logic
    fun addCategory(name: String, icon: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name, icon = icon))
        }
    }

    fun updateCategory(id: Int, name: String, icon: String, isDefault: Boolean = false) {
        viewModelScope.launch {
            repository.updateCategory(Category(id = id, name = name, icon = icon, isDefault = isDefault))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Adjustments Logic
    fun addAdjustment(amount: Double, note: String = "") {
        viewModelScope.launch {
            repository.insertAdjustment(Adjustment(amount = amount, note = note, timestamp = System.currentTimeMillis()))
        }
    }

    fun deleteAdjustment(adjustment: Adjustment) {
        viewModelScope.launch {
            repository.deleteAdjustment(adjustment)
        }
    }

    // State calculation engine
    private fun calculateBudgetState(
        allExpenses: List<Expense>,
        allAdjustments: List<Adjustment>,
        defaultBudget: Double,
        startDay: Int,
        startHour: Int,
        carryOver: Boolean
    ): BudgetState {
        val now = System.currentTimeMillis()
        val currentBusinessDate = BudgetCalendarHelper.getBusinessDate(now, startHour)
        
        // 1. Calculate boundaries of CURRENT billing cycle
        val currentCycleStart = BudgetCalendarHelper.getCycleStart(currentBusinessDate, startDay)
        val nextCycleStart = BudgetCalendarHelper.getNextCycleStart(currentCycleStart, startDay)
        
        val currentCycleStartTs = BudgetCalendarHelper.getBusinessDayStartTimestamp(currentCycleStart, startHour)
        val nextCycleStartTs = BudgetCalendarHelper.getBusinessDayStartTimestamp(nextCycleStart, startHour)

        // 2. Separate current month's transactions
        val currentCycleExpenses = allExpenses.filter { it.timestamp in currentCycleStartTs until nextCycleStartTs }
        val currentCycleAdjustments = allAdjustments.filter { it.timestamp in currentCycleStartTs until nextCycleStartTs }

        // 3. Compute PREVIOUS month's balance (savings / overspend)
        val prevCycleStart = BudgetCalendarHelper.getCycleStart(currentCycleStart.minusDays(1), startDay)
        val prevCycleStartTs = BudgetCalendarHelper.getBusinessDayStartTimestamp(prevCycleStart, startHour)
        val prevCycleEndTs = currentCycleStartTs // previous cycle ends exactly when current cycle starts

        val prevCycleExpensesSum = allExpenses
            .filter { it.timestamp in prevCycleStartTs until prevCycleEndTs }
            .sumOf { it.amount }

        val prevCycleAdjustmentsSum = allAdjustments
            .filter { it.timestamp in prevCycleStartTs until prevCycleEndTs }
            .sumOf { it.amount }

        // Previous Month Saving = Budget + Adjustments - Expenses
        // Reset savings to 0 if there are no expenses recorded in previous cycle
        val prevMonthBalance = if (prevCycleExpensesSum == 0.0) {
            0.0
        } else {
            defaultBudget + prevCycleAdjustmentsSum - prevCycleExpensesSum
        }

        // 4. Carry Over Logic
        val carryOverAmount = if (carryOver) prevMonthBalance else 0.0

        // 5. Compute Month Starting Budget
        val currentMonthAdjustmentsSum = currentCycleAdjustments.sumOf { it.amount }
        val totalMonthlyBudgetConfigured = defaultBudget + currentMonthAdjustmentsSum + carryOverAmount

        // 6. Days Remaining (inclusive of today)
        val daysRemainingInCycle = ChronoUnit.DAYS.between(currentBusinessDate, nextCycleStart).coerceAtLeast(1)

        // 7. Separate today's expenses
        // Today business date boundaries in physical time
        val todayStartTs = BudgetCalendarHelper.getBusinessDayStartTimestamp(currentBusinessDate, startHour)
        val todayEndTs = BudgetCalendarHelper.getBusinessDayEndTimestamp(currentBusinessDate, startHour)

        val todayExpenses = currentCycleExpenses.filter { it.timestamp in todayStartTs until todayEndTs }
        val todayExpensesSum = todayExpenses.sumOf { it.amount }

        // Previous days' expenses in this billing cycle
        val prevDaysExpensesSum = currentCycleExpenses
            .filter { it.timestamp < todayStartTs }
            .sumOf { it.amount }

        // Remaining month budget *prior* to today's expenses
        val remainingBudgetPriorToToday = totalMonthlyBudgetConfigured - prevDaysExpensesSum

        // Today's target daily allowance
        val todayDailyTargetAllowance = remainingBudgetPriorToToday / daysRemainingInCycle.toDouble()

        // Today's available daily budget (can be negative if spent too much)
        val todayAvailableDailyBudget = todayDailyTargetAllowance - todayExpensesSum

        // Tomorrow's allowance (recalculated based on current remaining budget including today's spending)
        val remainingBudgetAfterToday = remainingBudgetPriorToToday - todayExpensesSum
        val daysRemainingTomorrow = daysRemainingInCycle - 1
        
        val nextCycleEnd = BudgetCalendarHelper.getNextCycleStart(nextCycleStart, startDay)
        val totalDaysInNextCycle = ChronoUnit.DAYS.between(nextCycleStart, nextCycleEnd).toDouble()

        val tomorrowExpectedDailyBudget = if (daysRemainingTomorrow > 0) {
            remainingBudgetAfterToday / daysRemainingTomorrow.toDouble()
        } else {
            // Last day of the cycle: tomorrow starts a brand new month, so expected budget is next month's standard day allocation
            defaultBudget / totalDaysInNextCycle
        }

        return BudgetState(
            todaySpendableBudget = todayAvailableDailyBudget,
            todayTargetAllowance = todayDailyTargetAllowance,
            tomorrowExpectedBudget = tomorrowExpectedDailyBudget,
            remainingMonthBudget = remainingBudgetAfterToday,
            daysRemainingInCycle = daysRemainingInCycle.toInt(),
            todayExpensesSum = todayExpensesSum,
            previousMonthBalance = prevMonthBalance,
            currentMonthStartingBudget = totalMonthlyBudgetConfigured,
            cycleStart = currentCycleStart,
            cycleEnd = nextCycleStart.minusDays(1),
            prevCycleStart = prevCycleStart,
            prevCycleEnd = currentCycleStart.minusDays(1)
        )
    }
}

data class BudgetState(
    val todaySpendableBudget: Double = 0.0,
    val todayTargetAllowance: Double = 0.0,
    val tomorrowExpectedBudget: Double = 0.0,
    val remainingMonthBudget: Double = 0.0,
    val daysRemainingInCycle: Int = 1,
    val todayExpensesSum: Double = 0.0,
    val previousMonthBalance: Double = 0.0,
    val currentMonthStartingBudget: Double = 0.0,
    val cycleStart: LocalDate = LocalDate.now(),
    val cycleEnd: LocalDate = LocalDate.now(),
    val prevCycleStart: LocalDate = LocalDate.now(),
    val prevCycleEnd: LocalDate = LocalDate.now()
)
