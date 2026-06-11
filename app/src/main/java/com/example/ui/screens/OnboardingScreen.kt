package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SweetPrimary
import com.example.ui.theme.SweetPrimaryLight
import com.example.ui.theme.SweetBackground
import com.example.ui.theme.SweetTextDark
import com.example.viewmodel.BudgetViewModel
import com.example.ui.parseCurrency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: BudgetViewModel
) {
    var userName by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("€") }
    var startDay by remember { mutableStateOf(1) }
    var startHour by remember { mutableStateOf(0) }

    var currencyExpanded by remember { mutableStateOf(false) }
    var startDayExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Benvenuto!", fontWeight = FontWeight.ExtraBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Configuriamo la tua esperienza",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SweetTextDark
            )

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Il tuo nome") },
                placeholder = { Text("es. Marco") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { 
                    if (it.isEmpty() || it.replace(",", ".").toDoubleOrNull() != null || it.endsWith(".") || it.endsWith(",")) {
                        budget = it 
                    }
                },
                label = { Text("Budget mensile iniziale") },
                placeholder = { Text("es. 1000,00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Currency Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Valuta") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { currencyExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    listOf("€", "$", "£", "¥").forEach { sym ->
                        DropdownMenuItem(
                            text = { Text(sym) },
                            onClick = {
                                currency = sym
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }

            // Start Day Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "Giorno $startDay",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Giorno inizio mese") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { startDayExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = startDayExpanded,
                    onDismissRequest = { startDayExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f).heightIn(max = 300.dp)
                ) {
                    (1..31).forEach { day ->
                        DropdownMenuItem(
                            text = { Text("Giorno $day") },
                            onClick = {
                                startDay = day
                                startDayExpanded = false
                            }
                        )
                    }
                }
            }

            // Start Hour Selection (Refined to Hours Only)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SweetBackground)
                    .border(1.dp, SweetPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Orario inizio giornata",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    IconButton(
                        onClick = { if (startHour > 0) startHour-- },
                        modifier = Modifier.clip(CircleShape).background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = SweetPrimary)
                    }

                    Text(
                        text = String.format(Locale.ITALY, "%02d:00", startHour),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = SweetPrimary
                    )

                    IconButton(
                        onClick = { if (startHour < 11) startHour++ },
                        modifier = Modifier.clip(CircleShape).background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = SweetPrimary)
                    }
                }
                
                Text(
                    "Seleziona solo l'ora (max 11:00)",
                    style = MaterialTheme.typography.labelSmall,
                    color = SweetTextDark.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val budgetVal = budget.parseCurrency() ?: 0.0
                    val dayVal = startDay.coerceIn(1, 31)
                    val hourVal = startHour.coerceIn(0, 11)
                    val nameVal = if (userName.isBlank()) "User" else userName
                    
                    viewModel.completeOnboarding(
                        userNameStr = nameVal,
                        budget = budgetVal,
                        currency = currency,
                        startDay = dayVal,
                        startHour = hourVal
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary)
            ) {
                Text("Inizia ora", modifier = Modifier.padding(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }

            TextButton(
                onClick = { viewModel.skipOnboarding() }
            ) {
                Text("Salta per ora", color = SweetTextDark.copy(alpha = 0.6f))
            }
        }
    }
}
