package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.MonthlyBudget
import com.example.ui.components.DonutChart
import com.example.ui.components.ChartCategoryData
import com.example.ui.components.MonthlySavingsChart
import com.example.ui.components.MonthlyTrendPoint
import com.example.ui.components.TrendLineChart
import com.example.ui.theme.*
import com.example.viewmodel.BudgetViewModel
import com.example.ui.loc
import com.example.ui.formatCurrency
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

val ChartColors = listOf(
    PastelMint, PastelRose, PastelLavender, PastelBlue, PastelYellow, PastelPink, PastelPeach,
    SweetSecondary, SweetTertiary, Color(0xFF319795), Color(0xFF81E6D9), Color(0xFFFBB6CE)
)

@Composable
fun StatsScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val budgetState by viewModel.budgetState.collectAsState()
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val adjustments by viewModel.adjustments.collectAsState(initial = emptyList())
    val currency by viewModel.currencySymbol.collectAsState()
    val monthlyBudgets by viewModel.monthlyBudgets.collectAsState(initial = emptyList())
    
    val budgetValue by viewModel.monthlyBudget.collectAsState()
    val startDayVal by viewModel.billingStartDay.collectAsState()
    val startHourVal by viewModel.dayStartHour.collectAsState()

    var periodOffset by remember { mutableIntStateOf(0) } // 0 = current, 1 = prev, etc.
    var selectedCategoryIdForTrend by remember { mutableStateOf<Int?>(null) }

    // Helper to calculate data for a specific period
    fun getPeriodData(offset: Int): Pair<LocalDate, LocalDate> {
        var date = budgetState.cycleStart
        for (i in 0 until offset) {
            date = com.example.data.BudgetCalendarHelper.getCycleStart(date.minusDays(1), startDayVal)
        }
        val end = com.example.data.BudgetCalendarHelper.getNextCycleStart(date, startDayVal).minusDays(1)
        return date to end
    }

    val (currentPeriodStart, currentPeriodEnd) = getPeriodData(periodOffset)
    val periodLabel = if (periodOffset == 0) "Mese Corrente".loc() else "${currentPeriodStart.format(DateTimeFormatter.ofPattern("d MMM"))} - ${currentPeriodEnd.format(DateTimeFormatter.ofPattern("d MMM"))}"

    // Historical Points (last 6 months)
    val trendPoints = remember(expenses, adjustments, monthlyBudgets, budgetValue, startDayVal, startHourVal, budgetState) {
        val list = mutableListOf<MonthlyTrendPoint>()
        var cursor = budgetState.cycleStart
        val formatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())

        for (i in 0..5) {
            val start = com.example.data.BudgetCalendarHelper.getCycleStart(cursor, startDayVal)
            val nextStart = com.example.data.BudgetCalendarHelper.getNextCycleStart(start, startDayVal)
            val startTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(start, startHourVal)
            val nextTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(nextStart, startHourVal)

            val pExpenses = expenses.filter { it.timestamp in startTs until nextTs }
            val pAdjustments = adjustments.filter { it.timestamp in startTs until nextTs }
            val pBudget = monthlyBudgets.find { it.cycleStartDate == start.toString() }?.budget ?: budgetValue

            val totalSpent = pExpenses.sumOf { it.amount }
            val adjSum = pAdjustments.sumOf { it.amount }
            
            // Point 12.2: savings = budget - spent (excluding carryover but including adjustments of that period)
            val savings = pBudget + adjSum - totalSpent

            list.add(MonthlyTrendPoint(
                monthLabel = start.format(formatter).replaceFirstChar { it.uppercase() },
                amount = totalSpent,
                savings = savings,
                budget = pBudget + adjSum,
                categoriesAmount = pExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { e -> e.amount } }
            ))
            cursor = start.minusDays(1)
        }
        list.reverse()
        list
    }

    val monthsWithData = trendPoints.count { it.amount > 0 || it.budget != budgetValue }
    val isHistoricalAvailable = monthsWithData >= 2 // enabled from second month

    // Distribution for SELECTED period
    val selectedPeriodExpenses = remember(expenses, currentPeriodStart, currentPeriodEnd, startHourVal) {
        val sTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(currentPeriodStart, startHourVal)
        val eTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(currentPeriodEnd.plusDays(1), startHourVal)
        expenses.filter { it.timestamp in sTs until eTs }
    }

    val distribution = remember(selectedPeriodExpenses, categories) {
        selectedPeriodExpenses.groupBy { it.categoryId }.entries.mapIndexed { idx, entry ->
            val cat = categories.find { it.id == entry.key }
            ChartCategoryData(entry.key, cat?.name ?: "Altro", cat?.icon ?: "📦", entry.value.sumOf { it.amount }, ChartColors[idx % ChartColors.size])
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SweetBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Statistiche".loc(), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = SweetTextDark)

        // --- PERIOD NAVIGATION ---
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SweetSurface).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { periodOffset++ }) { Icon(Icons.Default.ArrowBackIos, contentDescription = null, modifier = Modifier.size(18.dp)) }
            Text(text = periodLabel, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black), color = SweetPrimary)
            IconButton(onClick = { if (periodOffset > 0) periodOffset-- }, enabled = periodOffset > 0) { 
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (periodOffset > 0) SweetPrimary else SweetTextLight) 
            }
        }

        // --- DISTRIBUTION CHART ---
        Text("Distribuzione per Categoria".loc(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = SweetTextDark)
        DonutChart(
            data = distribution,
            centerLabel = "Spesa Totale".loc(),
            centerValue = String.format("%s %s", currency, distribution.sumOf { it.amount }.formatCurrency()),
            currencySymbol = currency,
            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(24.dp))
        )

        // --- TRENDS ---
        Text("Andamento Storico".loc(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = SweetTextDark)
        
        if (!isHistoricalAvailable) {
            Card(colors = CardDefaults.cardColors(containerColor = SweetPrimaryLight), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = SweetPrimary)
                    Text("Le statistiche storiche saranno attive dal prossimo mese.".loc(), style = MaterialTheme.typography.bodySmall, color = SweetTextDark)
                }
            }
        }

        Box(modifier = Modifier.alpha(if (isHistoricalAvailable) 1f else 0.4f)) {
            TrendLineChart(
                points = trendPoints,
                selectedCategoryId = null,
                currencySymbol = currency,
                isEnabled = isHistoricalAvailable,
                modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(24.dp))
            )
        }

        Box(modifier = Modifier.alpha(if (isHistoricalAvailable) 1f else 0.4f)) {
            MonthlySavingsChart(
                points = trendPoints,
                currencySymbol = currency,
                isEnabled = isHistoricalAvailable,
                modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(24.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
