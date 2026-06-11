package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SweetPrimary
import com.example.ui.theme.SweetTextDark
import com.example.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: BudgetViewModel
) {
    var userName by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("€") }
    var startDay by remember { mutableStateOf("1") }
    var startHour by remember { mutableStateOf("0") }

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
                onValueChange = { budget = it },
                label = { Text("Budget mensile iniziale") },
                placeholder = { Text("es. 1000") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Valuta") },
                placeholder = { Text("es. €") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = startDay,
                onValueChange = { startDay = it },
                label = { Text("Giorno inizio mese") },
                placeholder = { Text("1-28") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = startHour,
                onValueChange = { startHour = it },
                label = { Text("Orario fine giornata (0-23)") },
                placeholder = { Text("es. 0") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val budgetVal = budget.toDoubleOrNull() ?: 0.0
                    val dayVal = startDay.toIntOrNull()?.coerceIn(1, 28) ?: 1
                    val hourVal = startHour.toIntOrNull()?.coerceIn(0, 23) ?: 0
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
