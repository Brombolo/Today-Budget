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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.ui.components.DonutChart
import com.example.ui.components.ChartCategoryData
import com.example.ui.components.MonthlySavingsChart
import com.example.ui.components.MonthlyTrendPoint
import com.example.ui.components.TrendLineChart
import com.example.ui.theme.*
import com.example.viewmodel.BudgetViewModel
import com.example.ui.loc
import java.time.format.DateTimeFormatter
import java.util.*

// Dynamic palette of candy/pastel colors for Category charts
val ChartColors = listOf(
    PastelMint,
    PastelRose,
    PastelLavender,
    PastelBlue,
    PastelYellow,
    PastelPink,
    PastelPeach,
    SweetSecondary,
    SweetTertiary,
    Color(0xFF319795),
    Color(0xFF81E6D9),
    Color(0xFFFBB6CE),
    Color(0xFFBEE3F8),
    Color(0xFFFEEBC8)
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
    
    val budgetValue by viewModel.monthlyBudget.collectAsState()
    val startDayVal by viewModel.billingStartDay.collectAsState()
    val startHourVal by viewModel.dayStartHour.collectAsState()

    var selectedCategoryIdForTrend by remember { mutableStateOf<Int?>(null) } // null = overall, non-null = single category
    var isDemoMode by remember { mutableStateOf(false) }

    // Dynamically calculate actual history trend points from the DB
    val actualTrendPoints = remember(expenses, adjustments, budgetState, budgetValue, startDayVal, startHourVal) {
        val list = mutableListOf<MonthlyTrendPoint>()
        val startHour = startHourVal
        val startDay = startDayVal
        val defaultBudget = budgetValue

        var cursorDate = budgetState.cycleStart
        val formatter = DateTimeFormatter.ofPattern("MMM", Locale.ITALIAN)

        // Loop for current and 3 prior months
        for (i in 0..3) {
            val currentCycleStart = com.example.data.BudgetCalendarHelper.getCycleStart(cursorDate, startDay)
            val nextCycleStart = com.example.data.BudgetCalendarHelper.getNextCycleStart(currentCycleStart, startDay)

            val cycleStartTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(currentCycleStart, startHour)
            val nextCycleStartTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(nextCycleStart, startHour)

            val cycleExpenses = expenses.filter { it.timestamp in cycleStartTs until nextCycleStartTs }
            val totalSpent = cycleExpenses.sumOf { it.amount }

            // Group expense by category
            val catGrouping = cycleExpenses.groupBy { it.categoryId }.mapValues { entry -> 
                entry.value.sumOf { it.amount }
            }

            // Adjustments in this cycle
            val cycleAdjustments = adjustments.filter { it.timestamp in cycleStartTs until nextCycleStartTs }
            val adjustmentsSum = cycleAdjustments.sumOf { it.amount }

            val savings = defaultBudget + adjustmentsSum - totalSpent

            list.add(
                MonthlyTrendPoint(
                    monthLabel = currentCycleStart.format(formatter).replaceFirstChar { it.uppercase() },
                    amount = totalSpent,
                    savings = savings,
                    categoriesAmount = catGrouping
                )
            )

            // Step cursor back to previous month's cycle
            cursorDate = currentCycleStart.minusDays(1)
        }
        list.reverse()
        list
    }

    // Toggle demo data if database is empty to show how visuals appear.
    val hasData = expenses.isNotEmpty()

    val trendPoints = if (isDemoMode || !hasData) {
        // High quality design mockup data for first install
        listOf(
            MonthlyTrendPoint("Mar", 680.0, 320.0, mapOf(1 to 350.0, 2 to 180.0, 3 to 100.0, 4 to 50.0)),
            MonthlyTrendPoint("Apr", 810.0, 190.0, mapOf(1 to 390.0, 2 to 210.0, 3 to 120.0, 4 to 90.0)),
            MonthlyTrendPoint("Mag", 520.0, 480.0, mapOf(1 to 280.0, 2 to 140.0, 3 to 60.0, 4 to 40.0)),
            MonthlyTrendPoint("Giu", 750.0, 250.0, mapOf(1 to 320.0, 2 to 200.0, 3 to 150.0, 4 to 80.0))
        )
    } else {
        actualTrendPoints
    }

    // Category summary statistics for CURRENT month
    val currentMonthExpenses = remember(expenses, budgetState) {
        val startTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(budgetState.cycleStart, startHourVal)
        val endTs = com.example.data.BudgetCalendarHelper.getBusinessDayStartTimestamp(budgetState.cycleEnd.plusDays(1), startHourVal)
        expenses.filter { it.timestamp in startTs until endTs }
    }

    val categoryDistribution = remember(currentMonthExpenses, categories, isDemoMode) {
        if (isDemoMode || currentMonthExpenses.isEmpty()) {
            // Mock categories distribution
            listOf(
                ChartCategoryData(1, "Cibo", "🍔", 320.0, ChartColors[0]),
                ChartCategoryData(2, "Casa", "🏠", 200.0, ChartColors[2]),
                ChartCategoryData(3, "Trasporti", "🚗", 150.0, ChartColors[3]),
                ChartCategoryData(4, "Tabacchi", "🚬", 80.0, ChartColors[1])
            )
        } else {
            val grouped = currentMonthExpenses.groupBy { it.categoryId }
            grouped.entries.mapIndexed { idx, entry ->
                val cat = categories.find { it.id == entry.key }
                val catName = cat?.name ?: "Altro"
                val catIcon = cat?.icon ?: "📦"
                val color = ChartColors[idx % ChartColors.size]
                ChartCategoryData(
                    categoryId = entry.key,
                    name = catName,
                    icon = catIcon,
                    amount = entry.value.sumOf { it.amount },
                    color = color
                )
            }
        }
    }

    val currentMonthTotal = categoryDistribution.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SweetBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Statistiche Spese".loc(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )
                Text(
                    text = "Grafici di andamento e categorie".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )
            }

            // Demo Mode toggle switch (beautifully customized)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SweetPrimaryLight)
                    .clickable { isDemoMode = !isDemoMode }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isDemoMode) "Dati Demo Attivi".loc() else "Attiva Demo".loc(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SweetPrimary
                )
            }
        }

        // --- PREVIOUS MONTH SAVINGS CARD ---
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (budgetState.previousMonthBalance >= 0) SweetPrimaryLight else PastelRose.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (budgetState.previousMonthBalance >= 0) "📈" else "📉", fontSize = 24.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Risparmio Mese Precedente".loc(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SweetTextLight
                    )
                    Text(
                        text = String.format("€%.2f", budgetState.previousMonthBalance),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = if (budgetState.previousMonthBalance >= 0) PastelMint else PastelRose,
                        modifier = Modifier.testTag("prev_month_savings")
                    )
                }
            }
        }

        // Empty Status Reminder
        if (!hasData && !isDemoMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SweetPrimaryLight)
                    .border(1.dp, SweetPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = SweetPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "I grafici reali prenderanno forma non appena registrerai delle spese. Al momento stai visualizzando dati demo rappresentativi.".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- DONUT CATEGORY CHART ---
        Text(
            text = "Distribuzione per Categoria".loc(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = SweetTextDark
        )

        DonutChart(
            data = categoryDistribution,
            centerLabel = "Totale Speso".loc(),
            centerValue = String.format("%s%.2f", currency, currentMonthTotal),
            currencySymbol = currency,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        )

        // --- TREND CHART FILTERS ---
        Text(
            text = "Andamento Storico".loc(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = SweetTextDark
        )

        // Filter pills for trend line chart
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isSelected = selectedCategoryIdForTrend == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SweetPrimary else SweetSurface)
                        .border(1.dp, if (isSelected) SweetPrimary else SweetCardGlow, RoundedCornerShape(12.dp))
                        .clickable { selectedCategoryIdForTrend = null }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Complessivo".loc(),
                        color = if (isSelected) Color.White else SweetTextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(categories) { cat ->
                val isSelected = selectedCategoryIdForTrend == cat.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SweetPrimary else SweetSurface)
                        .border(1.dp, if (isSelected) SweetPrimary else SweetCardGlow, RoundedCornerShape(12.dp))
                        .clickable { selectedCategoryIdForTrend = cat.id }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "${cat.icon} ${cat.name}",
                        color = if (isSelected) Color.White else SweetTextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- TREND LINE CHART ---
        val filterCatName = selectedCategoryIdForTrend?.let { id ->
            categories.find { it.id == id }?.name ?: "Categoria"
        } ?: "Totale Spese".loc()

        TrendLineChart(
            points = trendPoints,
            selectedCategoryId = selectedCategoryIdForTrend,
            selectedCategoryName = filterCatName,
            currencySymbol = currency,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        )

        // --- SAVINGS HISTORICAL TREND ---
        MonthlySavingsChart(
            points = trendPoints,
            currencySymbol = currency,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
