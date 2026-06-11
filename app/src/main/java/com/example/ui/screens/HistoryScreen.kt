package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.Expense
import com.example.viewmodel.BudgetViewModel
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.theme.*
import com.example.ui.loc
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
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val currency by viewModel.currencySymbol.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedExpenseForEdit by remember { mutableStateOf<Expense?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Expense?>(null) }

    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ITALIAN)

    // Filter logic
    val filteredExpenses = remember(expenses, categories, searchQuery) {
        expenses.filter { exp ->
            val catName = categories.find { it.id == exp.categoryId }?.name ?: "Altro"
            catName.contains(searchQuery, ignoreCase = true) ||
                    exp.description.contains(searchQuery, ignoreCase = true) ||
                    exp.amount.toString().contains(searchQuery)
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
        if (filteredExpenses.isEmpty()) {
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
                        text = "Nessuna spesa trovata".loc(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SweetTextDark
                    )
                    Text(
                        text = "Prova a cambiare filtri o inserisci una spesa.".loc(),
                        style = MaterialTheme.typography.bodySmall,
                        color = SweetTextLight,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredExpenses, key = { it.id }) { exp ->
                    val cat = categories.find { it.id == exp.categoryId }
                    val catName = cat?.name ?: "Altro"
                    val catIcon = cat?.icon ?: "📦"

                    val dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(exp.timestamp),
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
                            // Category Icon Bubble
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SweetPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = catIcon, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (exp.description.isNotEmpty()) exp.description else catName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SweetTextDark,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (exp.description.isNotEmpty()) {
                                        Text(
                                            text = catName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SweetPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(SweetTextLight))
                                    }
                                    Text(
                                        text = dateLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SweetTextLight
                                    )
                                }
                            }

                            // Price Tag
                            Text(
                                text = String.format("-%s%.2f", currency, exp.amount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = PastelRose,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            // Action button trigger
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { selectedExpenseForEdit = exp },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifica".loc(),
                                        tint = SweetPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { showDeleteConfirmDialog = exp },
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
            onDismiss = { selectedExpenseForEdit = null },
            onSave = { amount, categoryId, desc, ts ->
                viewModel.updateExpense(exp.id, amount, categoryId, desc, ts)
                selectedExpenseForEdit = null
            }
        )
    }

    // Modal Delete Trigger
    showDeleteConfirmDialog?.let { exp ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminare spesa?".loc(), fontWeight = FontWeight.Bold, color = SweetTextDark) },
            text = {
                Text(
                    "Sicuro di voler eliminare questa spesa di %s %s? L'operazione ricalcolerà immediatamente il budget.".loc(currency, String.format("%.2f", exp.amount)),
                    color = SweetTextLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp)
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
