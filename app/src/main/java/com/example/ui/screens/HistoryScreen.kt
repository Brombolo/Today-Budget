package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Expense
import com.example.viewmodel.BudgetViewModel
import com.example.viewmodel.HistoryItem
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.theme.*
import com.example.ui.loc
import com.example.ui.formatCurrency
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val history by viewModel.history.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val currency by viewModel.currencySymbol.collectAsState()
    val dayStartHour by viewModel.dayStartHour.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedExpenseForEdit by remember { mutableStateOf<Expense?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<HistoryItem?>(null) }

    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ITALIAN)

    // Filter logic
    val filteredHistory = remember(history, searchQuery) {
        history.filter { item ->
            when (item) {
                is HistoryItem.ExpenseItem -> {
                    val catName = item.category?.name ?: "Altro"
                    catName.contains(searchQuery, ignoreCase = true) ||
                            item.expense.description.contains(searchQuery, ignoreCase = true) ||
                            item.expense.amount.toString().contains(searchQuery)
                }
                is HistoryItem.AdjustmentItem -> {
                    "budget".contains(searchQuery, ignoreCase = true) ||
                            item.adjustment.note.contains(searchQuery, ignoreCase = true) ||
                            item.adjustment.amount.toString().contains(searchQuery)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SweetBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Cronologia Spese".loc(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = SweetTextDark
            )
            Text(
                text = "Modifica o elimina transazioni passate".loc(),
                style = MaterialTheme.typography.bodySmall,
                color = SweetTextLight
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cerca per descrizione o categoria...".loc()) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SweetTextLight) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SweetPrimary,
                unfocusedBorderColor = SweetCardGlow,
                focusedContainerColor = SweetSurface,
                unfocusedContainerColor = SweetSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(16.dp))
        )

        // Transaction list
        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = "🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nessuna voce trovata".loc(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SweetTextDark
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredHistory) { item ->
                    val dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(item.timestamp),
                        ZoneId.systemDefault()
                    )
                    val dateLabel = dateTime.format(formatter)

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SweetSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category Icon Bubble + Name aligned under
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SweetPrimaryLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val icon = when (item) {
                                        is HistoryItem.ExpenseItem -> item.category?.icon ?: "📦"
                                        is HistoryItem.AdjustmentItem -> "⚙️"
                                    }
                                    Text(text = icon, fontSize = 24.sp)
                                }
                                val subText = when (item) {
                                    is HistoryItem.ExpenseItem -> item.category?.name ?: "Altro"
                                    is HistoryItem.AdjustmentItem -> "Budget"
                                }
                                Text(
                                    text = subText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SweetTextLight,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                val title = when (item) {
                                    is HistoryItem.ExpenseItem -> if (item.expense.description.isNotEmpty()) item.expense.description else (item.category?.name ?: "Spesa")
                                    is HistoryItem.AdjustmentItem -> item.adjustment.note
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SweetTextDark,
                                    maxLines = 1
                                )
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SweetTextLight
                                )
                            }

                            // Price Tag
                            val amount = when (item) {
                                is HistoryItem.ExpenseItem -> item.expense.amount
                                is HistoryItem.AdjustmentItem -> item.adjustment.amount
                            }
                            val isNegative = amount < 0 || item is HistoryItem.ExpenseItem
                            val absAmount = Math.abs(amount)
                            
                            Text(
                                text = (if (isNegative) "-" else "+") + currency + absAmount.formatCurrency(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = if (isNegative) PastelRose else PastelMint,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            // Action button trigger
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item is HistoryItem.ExpenseItem) {
                                    IconButton(
                                        onClick = { selectedExpenseForEdit = item.expense },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Modifica".loc(),
                                            tint = SweetPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { showDeleteConfirmDialog = item },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina".loc(),
                                        tint = PastelRose,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal AddEdit Trigger
    selectedExpenseForEdit?.let { exp ->
        AddEditExpenseDialog(
            categories = categories,
            expenseToEdit = exp,
            currencySymbol = currency,
            dayStartHour = dayStartHour,
            onDismiss = { selectedExpenseForEdit = null },
            onSave = { amount, categoryId, desc, ts ->
                viewModel.updateExpense(exp.id, amount, categoryId, desc, ts)
                selectedExpenseForEdit = null
            }
        )
    }

    // Modal Delete Trigger
    showDeleteConfirmDialog?.let { item ->
        val (typeLabel, amt) = when (item) {
            is HistoryItem.ExpenseItem -> "spesa" to item.expense.amount
            is HistoryItem.AdjustmentItem -> "rettifica" to item.adjustment.amount
        }
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminare $typeLabel?".loc(), fontWeight = FontWeight.Bold, color = SweetTextDark) },
            text = {
                Text(
                    "Sicuro di voler eliminare questa $typeLabel di %s %s? L'operazione ricalcolerà immediatamente il budget.".loc(currency, amt.formatCurrency()),
                    color = SweetTextLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (item) {
                            is HistoryItem.ExpenseItem -> viewModel.deleteExpense(item.expense)
                            is HistoryItem.AdjustmentItem -> viewModel.deleteAdjustment(item.adjustment)
                        }
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelRose)
                ) {
                    Text("Elimina".loc(), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Annulla".loc(), color = SweetTextLight)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SweetSurface
        )
    }
}
