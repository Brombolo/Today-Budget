package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.BudgetViewModel
import com.example.viewmodel.HistoryItem
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.components.AdjustBudgetDialog
import com.example.ui.theme.*
import com.example.ui.loc
import com.example.ui.formatCurrency
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun HomeScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier,
    onNavigateToTab: ((Int) -> Unit)? = null
) {
    val budgetState by viewModel.budgetState.collectAsState()
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val history by viewModel.history.collectAsState(initial = emptyList())
    val currency by viewModel.currencySymbol.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val carryOverEnabled by viewModel.carryOverEnabled.collectAsState()
    val pinnedIds by viewModel.pinnedCategoryIds.collectAsState()
    val dayStartHour by viewModel.dayStartHour.collectAsState()

    var showExpenseDialog by remember { mutableStateOf(false) }
    var showAdjustDialog by remember { mutableStateOf(false) }
    var quickActionCategoryId by remember { mutableStateOf<Int?>(null) }

    // Logic for temporary budget warning popup
    var showWarningPopup by remember { mutableStateOf(false) }
    
    // Track negative state changes
    var wasPreviouslyPositive by remember { mutableStateOf(budgetState.todaySpendableBudget >= 0) }
    
    LaunchedEffect(budgetState.todaySpendableBudget) {
        val currentlyNegative = budgetState.todaySpendableBudget < 0
        if (currentlyNegative && wasPreviouslyPositive) {
            showWarningPopup = true
            delay(2000) // Show for 2 seconds
            showWarningPopup = false
        }
        wasPreviouslyPositive = !currentlyNegative
    }

    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SweetBackground)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today Budget",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = SweetPrimary
                    )
                    Text(
                        text = "Ciao, %s! 👋".loc(userName),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = SweetTextLight
                    )
                }

                // Point 6: Mese precedente info in top right
                if (carryOverEnabled) {
                    val positiveCarryOver = budgetState.previousMonthBalance > 0
                    val absCarryOver = Math.abs(budgetState.previousMonthBalance)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SweetSurface),
                        modifier = Modifier.shadow(1.dp, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Mese precedente".loc(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                                color = SweetTextLight
                            )
                            Text(
                                text = (if (positiveCarryOver) "+" else "-") + currency + " " + absCarryOver.formatCurrency(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                color = if (positiveCarryOver) PastelMint else PastelRose
                            )
                        }
                    }
                }
            }

            // --- Main Hero Card: Daily Budget ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(32.dp), clip = false)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFD0BCFF), Color(0xFFEADDFF)),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp)
            ) {
                // Point 9: Total remaining budget in top right of card
                val totalRemaining = budgetState.remainingMonthBudget
                val isTotalNegative = totalRemaining < 0
                val absTotalRemaining = Math.abs(totalRemaining)
                Text(
                    text = (if (isTotalNegative) "-" else "+") + currency + " " + (if (absTotalRemaining < 0.01) "0,00" else absTotalRemaining.formatCurrency()),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp),
                    color = if (isTotalNegative) PastelRose else Color(0xFF1B5E20),
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "DISPONIBILE OGGI".loc(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = SweetSecondary.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%s %s", currency, budgetState.todaySpendableBudget.formatCurrency()),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp
                                ),
                                color = SweetSecondary,
                                modifier = Modifier.testTag("today_available_budget")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (budgetState.daysRemainingInCycle > 1) {
                            Column {
                                Text(
                                    text = "PREVISTO DOMANI".loc(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = SweetSecondary.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = String.format("%s %s", currency, budgetState.tomorrowExpectedBudget.formatCurrency()),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SweetSecondary,
                                    modifier = Modifier.testTag("tomorrow_expected_budget")
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "FINE MESE: %s".loc(budgetState.cycleEnd.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = SweetSecondary.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${budgetState.daysRemainingInCycle} ${"giorni rimasti".loc()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SweetSecondary
                            )
                        }
                    }
                }
            }

            // --- Quick Category Actions + Moved Core Actions ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "NUOVA SPESA RAPIDA".loc(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = SweetTextLight,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                val displayCategories = if (pinnedIds.isEmpty()) {
                    categories.take(4)
                } else {
                    categories.filter { pinnedIds.contains(it.id) }
                }

                if (displayCategories.isNotEmpty()) {
                    displayCategories.chunked(2).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCats.forEach { cat ->
                                val cardBg = when (cat.id % 4) {
                                    0 -> Color(0xFFE8DEF8)
                                    1 -> Color(0xFFFEE4E2)
                                    2 -> Color(0xFFD1FADF)
                                    else -> Color(0xFFFEF0C7)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(cardBg)
                                        .clickable {
                                            quickActionCategoryId = cat.id
                                            showExpenseDialog = true
                                        }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = cat.icon, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = cat.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = SweetSecondary,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            if (rowCats.size == 1) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
                
                // --- Core Control Actions ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showAdjustDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SweetPrimary),
                        border = BorderStroke(1.5.dp, SweetPrimary),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(0.8f)
                            .height(44.dp)
                            .testTag("correct_budget_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Regola".loc(),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Correggi".loc(), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = { showExpenseDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(54.dp)
                            .shadow(3.dp, RoundedCornerShape(18.dp))
                            .testTag("add_expense_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Spesa".loc(),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nuova Spesa".loc(), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // --- Monthly Progress Mini-Widget ---
            val spentAmount = (budgetState.currentMonthStartingBudget - budgetState.remainingMonthBudget).coerceAtLeast(0.0)
            val spentPercentage = if (budgetState.currentMonthStartingBudget > 0.0) {
                (spentAmount / budgetState.currentMonthStartingBudget).coerceIn(0.0, 1.0)
            } else {
                0.0
            }
            val animPercent by animateFloatAsState(
                targetValue = spentPercentage.toFloat(),
                animationSpec = tween(1000)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SweetSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 8f
                            drawCircle(
                                color = SweetBackground,
                                radius = size.minDimension / 2 - strokeWidth,
                                style = Stroke(width = strokeWidth)
                            )
                            drawArc(
                                color = SweetPrimary,
                                startAngle = -90f,
                                sweepAngle = animPercent * 360f,
                                useCenter = false,
                                topLeft = Offset(strokeWidth, strokeWidth),
                                size = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = String.format("%d%%", (spentPercentage * 100).toInt()),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = SweetTextDark
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BUDGET MENSILE IN CORSO".loc(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = SweetTextLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%s %s spesi / %s %s".loc(currency, spentAmount.formatCurrency(), currency, budgetState.currentMonthStartingBudget.formatCurrency()),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = SweetTextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(SweetBackground)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = animPercent)
                                    .clip(CircleShape)
                                    .background(SweetPrimary)
                            )
                        }
                    }
                }
            }

            // --- Recent History Card ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SweetSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cronologia Recente".loc(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = SweetTextDark
                        )
                        Text(
                            text = "VEDI TUTTO".loc(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = SweetPrimary,
                            modifier = Modifier
                                .clickable { onNavigateToTab?.invoke(1) }
                                .padding(4.dp)
                        )
                    }

                    val recentHistoryList = history.take(5) // Point 1: Last 5 transactions
                    if (recentHistoryList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nessuna spesa oggi. Comincia a risparmiare!".loc(),
                                style = MaterialTheme.typography.labelMedium,
                                color = SweetTextLight
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            recentHistoryList.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(SweetBackground),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val icon = when (item) {
                                                is HistoryItem.ExpenseItem -> item.category?.icon ?: "💸"
                                                is HistoryItem.AdjustmentItem -> "⚙️"
                                            }
                                            Text(text = icon, fontSize = 16.sp)
                                        }

                                        Column {
                                            val title = when (item) {
                                                is HistoryItem.ExpenseItem -> if (item.expense.description.isNotEmpty()) item.expense.description else (item.category?.name ?: "Spesa".loc())
                                                is HistoryItem.AdjustmentItem -> item.adjustment.note
                                            }
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = SweetTextDark
                                            )
                                            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(item.timestamp), ZoneId.systemDefault())
                                            Text(
                                                text = "${ldt.format(formatter)}, ${ldt.format(timeFormatter)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SweetTextLight
                                            )
                                        }
                                    }

                                    val amount = when (item) {
                                        is HistoryItem.ExpenseItem -> item.expense.amount
                                        is HistoryItem.AdjustmentItem -> item.adjustment.amount
                                    }
                                    val isNegative = amount < 0 || item is HistoryItem.ExpenseItem
                                    val absAmount = Math.abs(amount)

                                    Text(
                                        text = (if (isNegative) "- " else "+ ") + currency + " " + absAmount.formatCurrency(),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                        color = if (isNegative) PastelRose else PastelMint
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Temporary Warning Popup Overlay (Placed in Box to avoid Column scroll) ---
        AnimatedVisibility(
            visible = showWarningPopup,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PastelRose,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Budget superato!".loc(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    // Modal Overlays
    if (showExpenseDialog) {
        AddEditExpenseDialog(
            categories = categories,
            initialCategoryId = quickActionCategoryId,
            currencySymbol = currency,
            dayStartHour = dayStartHour,
            onDismiss = { 
                showExpenseDialog = false
                quickActionCategoryId = null
            },
            onSave = { amount, categoryId, desc, ts ->
                viewModel.addExpense(amount, categoryId, desc, ts)
                showExpenseDialog = false
                quickActionCategoryId = null
            }
        )
    }

    if (showAdjustDialog) {
        AdjustBudgetDialog(
            currencySymbol = currency,
            onDismiss = { showAdjustDialog = false },
            onSave = { amount, note ->
                viewModel.addAdjustment(amount, note)
                showAdjustDialog = false
            }
        )
    }
}
