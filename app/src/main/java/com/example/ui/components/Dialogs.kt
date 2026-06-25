package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.Expense
import com.example.ui.theme.*

val PresetEmojis = listOf(
    "🍔", "🏠", "🚗", "🚬", "🛍️", "🍿", "💡", "💊",
    "✈️", "🎮", "🧴", "🍕", "☕", "🥩", "🚌", "🐕",
    "💇", "👕", "📚", "🎁", "❤️", "📦", "🧩", "💰",
    "🐈", "🍺", "🍷", "📱", "💻", "🎬", "🎫", "🎸",
    "⚽", "🏋️", "🧘", "🧗", "🏕️", "🧹", "🔨", "🛋️",
    "🔌", "📶", "🚰", "💈", "💄", "💍", "💎", "🩺",
    "🦷", "🩹", "🏛️", "💳", "🌾", "💼", "✉️", "🎯",
    "🐟", "🍣", "🍗", "🥐", "🍞", "🍩", "🍰", "🍎",
    "🥦", "🥚", "🥛", "⚡", "💧", "⛽", "🗑️", "👗",
    "👟", "🚲", "🧼", "📰", "💇‍♂️", "🧴", "🧸", "🕯️"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    categories: List<Category>,
    expenseToEdit: Expense? = null,
    initialCategoryId: Int? = null,
    currencySymbol: String = "€",
    dayStartHour: Int = 0,
    onDismiss: () -> Unit,
    onSave: (amount: Double, categoryId: Int, description: String, timestamp: Long) -> Unit
) {
    var amountStr by remember { mutableStateOf(expenseToEdit?.amount?.let { String.format(java.util.Locale.ITALY, "%.2f", it) } ?: "") }
    var description by remember { mutableStateOf(expenseToEdit?.description ?: "") }
    var selectedCategoryId by remember { mutableStateOf(expenseToEdit?.categoryId ?: initialCategoryId ?: categories.firstOrNull()?.id ?: 0) }
    var errorMessage by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Date/Time State
    val initialTimestamp = expenseToEdit?.timestamp ?: System.currentTimeMillis()
    var selectedDateTime by remember { mutableStateOf(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(initialTimestamp), java.time.ZoneId.systemDefault())) }

    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.ITALY)
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm", java.util.Locale.ITALY)

    fun isToday(date: java.time.LocalDate): Boolean {
        return date.isEqual(java.time.LocalDate.now())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (expenseToEdit == null) "Aggiungi Spesa" else "Modifica Spesa",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SweetTextDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Amount Field
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        if (it.isEmpty() || it.replace(",", ".").toDoubleOrNull() != null || it.endsWith(".") || it.endsWith(",")) {
                            amountStr = it 
                        }
                    },
                    label = { Text("Importo ($currencySymbol)") },
                    placeholder = { Text("0,00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date and Time Pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Button
                    OutlinedButton(
                        onClick = {
                            val dialog = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newDate = java.time.LocalDate.of(year, month + 1, dayOfMonth)
                                    val newDateTime = selectedDateTime.with(newDate)
                                    
                                    // Point 1 Logic: if new expense, auto-adjust time
                                    if (expenseToEdit == null) {
                                        selectedDateTime = if (isToday(newDate)) {
                                            val now = java.time.LocalDateTime.now()
                                            newDateTime.withHour(now.hour).withMinute(now.minute)
                                        } else {
                                            newDateTime.withHour(dayStartHour).withMinute(0)
                                        }
                                    } else {
                                        // Point 2 Logic: for edit, don't auto-adjust time
                                        selectedDateTime = newDateTime
                                    }
                                },
                                selectedDateTime.year,
                                selectedDateTime.monthValue - 1,
                                selectedDateTime.dayOfMonth
                            )
                            dialog.show()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Data", style = MaterialTheme.typography.labelSmall, color = SweetTextLight)
                            Text(selectedDateTime.format(dateFormatter), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SweetTextDark)
                        }
                    }

                    // Time Button
                    OutlinedButton(
                        onClick = {
                            val dialog = android.app.TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    selectedDateTime = selectedDateTime.withHour(hour).withMinute(minute)
                                },
                                selectedDateTime.hour,
                                selectedDateTime.minute,
                                true
                            )
                            dialog.show()
                        },
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ora", style = MaterialTheme.typography.labelSmall, color = SweetTextLight)
                            Text(selectedDateTime.format(timeFormatter), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SweetTextDark)
                        }
                    }
                }

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { 
                        if (it.length <= 22) description = it 
                    },
                    label = { Text("Nota / Descrizione") },
                    placeholder = { Text("es. Spesa settimanale") },
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "${description.length} / 22",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection with explicit indicator
                Text(
                    text = "Seleziona Categoria:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SweetTextDark,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (categories.isEmpty()) {
                    Text(
                        text = "Crea prima una categoria!",
                        color = PastelRose,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategoryId == cat.id
                            val bg = if (isSelected) SweetPrimaryLight else Color.Transparent
                            val borderCol = if (isSelected) SweetPrimary else SweetCardGlow
                            val textColor = if (isSelected) SweetPrimary else SweetTextDark

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .border(1.5.dp, borderCol, RoundedCornerShape(12.dp))
                                    .clickable { selectedCategoryId = cat.id }
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = cat.icon, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = textColor,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = PastelRose, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.replace(",", ".").toDoubleOrNull()
                    if (amt == null || amt <= 0.0) {
                        errorMessage = "Inserisci un importo maggiore di zero."
                        return@Button
                    }
                    if (selectedCategoryId == 0 && categories.isNotEmpty()) {
                        selectedCategoryId = categories.first().id
                    }
                    onSave(
                        amt,
                        selectedCategoryId,
                        description,
                        selectedDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary)
            ) {
                Text("Salva", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = SweetTextLight)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = SweetSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustBudgetDialog(
    currencySymbol: String = "€",
    onDismiss: () -> Unit,
    onSave: (amount: Double, note: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var isPositive by remember { mutableStateOf(true) } // true for inflow (+), false for adjustment (-)
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Correggi Budget",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SweetTextDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "In questa sezione si possono aggiungere o rimuovere importi dal budget complessivo impostato.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )

                // Inflow or Outflow Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isPositive = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPositive) PastelMint else SweetBackground
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Positivo",
                            tint = if (isPositive) Color.White else SweetTextLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Entrata (+)",
                            color = if (isPositive) Color.White else SweetTextDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { isPositive = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isPositive) PastelRose else SweetBackground
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Negativo",
                            tint = if (!isPositive) Color.White else SweetTextLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Rettifica (-)",
                            color = if (!isPositive) Color.White else SweetTextDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                            amountStr = it 
                        }
                    },
                    label = { Text("Importo ($currencySymbol)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { 
                        if (it.length <= 22) note = it 
                    },
                    label = { Text("Nota / Motivo") },
                    placeholder = { Text("es. Rimborso, Regalo, etc.") },
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "${note.length} / 22",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = PastelRose, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawAmt = amountStr.toDoubleOrNull()
                    if (rawAmt == null || rawAmt <= 0.0) {
                        errorMessage = "Inserisci un importo valido."
                        return@Button
                    }
                    val finalAmt = if (isPositive) rawAmt else -rawAmt
                    val finalNote = if (note.trim().isEmpty()) {
                        if (isPositive) "Entrata Straordinaria" else "Rettifica Budget"
                    } else {
                        note.trim()
                    }
                    onSave(finalAmt, finalNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary)
            ) {
                Text("Salva", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = SweetTextLight)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = SweetSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryDialog(
    categoryToEdit: Category? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf(categoryToEdit?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(categoryToEdit?.icon ?:PresetEmojis.first()) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (categoryToEdit == null) "Nuova Categoria" else "Modifica Categoria",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SweetTextDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        if (it.length <= 12) name = it 
                    },
                    label = { Text("Nome Categoria") },
                    placeholder = { Text("es. Casa") },
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "${name.length} / 12",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Scegli Simbolo / Emoji:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SweetTextDark,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Selector Grid of presets
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PresetEmojis) { emoji ->
                        val isSelected = selectedIcon == emoji
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) SweetPrimaryLight else Color.Transparent)
                                .border(1.5.dp, if (isSelected) SweetPrimary else Color.Transparent, CircleShape)
                                .clickable { selectedIcon = emoji }
                                .padding(4.dp)
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = PastelRose, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        errorMessage = "Il nome non può essere vuoto."
                        return@Button
                    }
                    onSave(name.trim(), selectedIcon)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary)
            ) {
                Text("Salva", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = SweetTextLight)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = SweetSurface
    )
}
