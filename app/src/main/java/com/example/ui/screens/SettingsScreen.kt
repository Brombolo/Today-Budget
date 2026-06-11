package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BudgetViewModel
import com.example.ui.loc
import com.example.ui.parseCurrency
import com.example.ui.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val budgetVal by viewModel.monthlyBudget.collectAsState()
    val startDayVal by viewModel.billingStartDay.collectAsState()
    val startHourVal by viewModel.dayStartHour.collectAsState()
    val carryOverVal by viewModel.carryOverEnabled.collectAsState()

    val currencyVal by viewModel.currencySymbol.collectAsState()
    val pushDailyVal by viewModel.pushDailyEnabled.collectAsState()
    val pushDailyTimeVal by viewModel.pushDailyTime.collectAsState()
    val pushWeeklyMonthlyVal by viewModel.pushWeeklyMonthlyEnabled.collectAsState()
    val pushBudgetConfirmVal by viewModel.pushBudgetConfirmEnabled.collectAsState()

    val userNameVal by viewModel.userName.collectAsState()
    var userName by remember(userNameVal) { mutableStateOf(userNameVal) }

    // Internal states synced to preferences on init / updates
    var budgetStr by remember(budgetVal) { mutableStateOf(budgetVal.formatCurrency()) }
    var startDay by remember(startDayVal) { mutableStateOf(startDayVal) }
    var startHour by remember(startHourVal) { mutableStateOf(startHourVal) }
    var carryOverEnabled by remember(carryOverVal) { mutableStateOf(carryOverVal) }

    var currency by remember(currencyVal) { mutableStateOf(currencyVal) }
    var pushDailyEnabled by remember(pushDailyVal) { mutableStateOf(pushDailyVal) }
    var pushDailyTime by remember(pushDailyTimeVal) { mutableStateOf(pushDailyTimeVal) }
    var pushWeeklyMonthlyEnabled by remember(pushWeeklyMonthlyVal) { mutableStateOf(pushWeeklyMonthlyVal) }
    var pushBudgetConfirmEnabled by remember(pushBudgetConfirmVal) { mutableStateOf(pushBudgetConfirmVal) }

    var isSavedVisible by remember { mutableStateOf(false) }

    var isNotificationPermissionGranted by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isNotificationPermissionGranted = isGranted
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SweetBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Impostazioni".loc(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = SweetTextDark
            )
            Text(
                text = "Personalizza regole, orari e calcolo del budget".loc(),
                style = MaterialTheme.typography.bodySmall,
                color = SweetTextLight
            )
        }

        // --- PROFILO UTENTE ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Profilo Utente".loc(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )

                Divider(color = SweetCardGlow)

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nome Utente".loc()) },
                    placeholder = { Text("Marco") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_username_input")
                )
            }
        }

        // --- SECTION A: DEFAULT BUDGET ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Imposta Budget mensile".loc(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )

                Divider(color = SweetCardGlow)

                OutlinedTextField(
                    value = budgetStr,
                    onValueChange = { 
                        if (it.isEmpty() || it.replace(",", ".").toDoubleOrNull() != null || it.endsWith(".") || it.endsWith(",")) {
                            budgetStr = it 
                        }
                    },
                    label = { Text("Budget Mensile Predefinito (%s)".loc(currency)) },
                    placeholder = { Text("0,00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SweetPrimary,
                        unfocusedBorderColor = SweetCardGlow
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_budget_input")
                )
            }
        }

        // --- SECTION B: CHOOSE CURRENCY PANEL ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Valuta dell'App".loc(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )

                Divider(color = SweetCardGlow)

                Text(
                    text = "Seleziona il simbolo monetario che preferisci utilizzare per il budget e le spese.".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currencies = listOf("€", "$", "£", "¥")
                    currencies.forEach { sym ->
                        val isSelected = currency == sym
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SweetPrimary else SweetBackground)
                                .border(1.5.dp, if (isSelected) SweetPrimary else SweetCardGlow, RoundedCornerShape(12.dp))
                                .clickable { currency = sym }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = sym,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = if (isSelected) Color.White else SweetTextDark
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION C: MONTHLY START DAY ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Giorno Inizio Ciclo".loc(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )

                Divider(color = SweetCardGlow)

                Text(
                    text = "Definisci in quale giorno del mese solare inizia ufficialmente il conteggio del tuo budget.".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )

                // Input control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            startDay = if (startDay > 1) startDay - 1 else 31 
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrementa".loc(), tint = SweetPrimary)
                    }

                    Text(
                        text = "Giorno $startDay",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = SweetTextDark,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .testTag("settings_start_day_text")
                    )

                    IconButton(
                        onClick = { 
                            startDay = if (startDay < 31) startDay + 1 else 1 
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Incrementa".loc(), tint = SweetPrimary)
                    }
                }

                // Friendly Hint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SweetBackground)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Ad esempio, impostando il giorno '%d', il tuo periodo di budget attuale andrà dal %d di questo mese al giorno %d del mese successivo.".loc(
                            startDay,
                            startDay,
                            if (startDay == 1) 30 else startDay - 1
                        ),
                        fontSize = 11.sp,
                        color = SweetTextLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // --- SECTION D: HOUR START OF DAY ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ora Inizio Giornata".loc(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )

                Divider(color = SweetCardGlow)

                Text(
                    text = "Imposta l'orario in cui il budget giornaliero si azzera e inizia la giornata successiva (es. alle 04:00).".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (startHour > 0) startHour-- },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Indietro".loc(), tint = SweetPrimary)
                    }

                    Text(
                        text = String.format("%02d:00", startHour),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = SweetTextDark,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .testTag("settings_start_hour_text")
                    )

                    IconButton(
                        onClick = { if (startHour < 11) startHour++ },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SweetPrimaryLight)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Avanti".loc(), tint = SweetPrimary)
                    }
                }

                // Friendly Hint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SweetBackground)
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (startHour == 0) {
                            "Orario standard a mezzanotte (00:00). Tutte le spese inserite dopo mezzanotte apparterranno alla nuova giornata.".loc()
                        } else {
                            "Le spese effettuate fino alle ore %s del mattino saranno dedotte dal budget della giornata precedente.".loc(String.format("%02d:00", startHour))
                        },
                        fontSize = 11.sp,
                        color = SweetTextLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // --- SECTION E: CARRY OVER OPTIONS ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                        Text(
                            text = "Risparmio/Carry Over".loc(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SweetTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Attiva per cumulare al budget odierno i risparmi (o debiti) dei giorni e dei mesi precedenti.".loc(),
                            style = MaterialTheme.typography.bodySmall,
                            color = SweetTextLight
                        )
                    }

                    Switch(
                        checked = carryOverEnabled,
                        onCheckedChange = { carryOverEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SweetPrimary,
                            uncheckedThumbColor = SweetTextLight,
                            uncheckedTrackColor = SweetCardGlow
                        ),
                        modifier = Modifier.testTag("settings_carry_over_switch")
                    )
                }
            }
        }

        // --- SECTION F: PUSH NOTIFICATIONS ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SweetSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifiche Push Personalizzate".loc(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isNotificationPermissionGranted) SweetTextDark else SweetTextLight,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isNotificationPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    isNotificationPermissionGranted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Abilita".loc(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider(color = SweetCardGlow)

                // Sub controls grayer/disabled until notifications are requested and authorized
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isNotificationPermissionGranted) 1.0f else 0.5f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Promemoria Scrittura Spese Giornaliere
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                Text(
                                    text = "Inserimento Spese Giornaliere".loc(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SweetTextDark
                                )
                                Text(
                                    text = "Ricevi un promemoria all'orario indicato per inserire le spese della giornata.".loc(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SweetTextLight
                                )
                            }
                            Switch(
                                checked = pushDailyEnabled,
                                onCheckedChange = { pushDailyEnabled = it },
                                enabled = isNotificationPermissionGranted,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SweetPrimary,
                                    uncheckedThumbColor = SweetTextLight,
                                    uncheckedTrackColor = SweetCardGlow
                                )
                            )
                        }

                        if (pushDailyEnabled && isNotificationPermissionGranted) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SweetBackground)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Orario: %s".loc(pushDailyTime),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SweetTextDark
                                )
                                Button(
                                    onClick = {
                                        val parts = pushDailyTime.split(":")
                                        val defaultHour = parts.getOrNull(0)?.toIntOrNull() ?: 20
                                        val defaultMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                        android.app.TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                pushDailyTime = String.format("%02d:%02d", hour, minute)
                                            },
                                            defaultHour,
                                            defaultMinute,
                                            true
                                        ).show()
                                    },
                                    enabled = isNotificationPermissionGranted,
                                    colors = ButtonDefaults.buttonColors(containerColor = SweetPrimaryLight),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Modifica Orario".loc(), color = SweetPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Divider(color = SweetCardGlow.copy(alpha = 0.5f))

                    // 2. Notifiche Riepilogo Settimanale/Mensile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                            Text(
                                text = "Riepilogo Spese Settimanale/Mensile".loc(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SweetTextDark
                            )
                            Text(
                                text = "Invia notifiche per rimanere aggiornato sull'andamento globale.".loc(),
                                style = MaterialTheme.typography.bodySmall,
                                color = SweetTextLight
                            )
                        }
                        Switch(
                            checked = pushWeeklyMonthlyEnabled,
                            onCheckedChange = { pushWeeklyMonthlyEnabled = it },
                            enabled = isNotificationPermissionGranted,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SweetPrimary,
                                uncheckedThumbColor = SweetTextLight,
                                uncheckedTrackColor = SweetCardGlow
                            )
                        )
                    }

                    Divider(color = SweetCardGlow.copy(alpha = 0.5f))

                    // 3. Conferma/Modifica Budget Primo Giorno del Mese
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                            Text(
                                text = "Notifica Conferma Budget".loc(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SweetTextDark
                            )
                            Text(
                                text = "Ricevi un avviso il primo giorno del tuo ciclo di budget per confermarlo o adeguarlo.".loc(),
                                style = MaterialTheme.typography.bodySmall,
                                color = SweetTextLight
                            )
                        }
                        Switch(
                            checked = pushBudgetConfirmEnabled,
                            onCheckedChange = { pushBudgetConfirmEnabled = it },
                            enabled = isNotificationPermissionGranted,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SweetPrimary,
                                uncheckedThumbColor = SweetTextLight,
                                uncheckedTrackColor = SweetCardGlow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // --- FLOATING FEEDBACK ALERT ---
        AnimatedVisibility(visible = isSavedVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PastelMint.copy(alpha = 0.15f))
                    .border(1.dp, PastelMint.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "OK", tint = PastelMint)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Impostazioni Salvate Correttamente!".loc(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SweetTextDark)
            }
        }

        // --- CORE SAVE BUTTON ---
        Button(
            onClick = {
                val budget = budgetStr.parseCurrency()
                if (budget == null || budget < 0.0) {
                    Toast.makeText(context, "Inserisci un budget mensile valido.".loc(), Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.updateSettings(
                    budget = budget,
                    startDay = startDay,
                    startHour = startHour,
                    carryOver = carryOverEnabled,
                    userNameStr = if (userName.isBlank()) "Marco" else userName
                )
                viewModel.updateNotificationAndCurrencySettings(
                    currency = currency,
                    pushDaily = pushDailyEnabled,
                    pushDailyTimeVal = pushDailyTime,
                    pushWeeklyMonthly = pushWeeklyMonthlyEnabled,
                    pushBudgetConfirm = pushBudgetConfirmEnabled
                )
                isSavedVisible = true
                Toast.makeText(context, "Impostazioni salvate!".loc(), Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .testTag("save_settings_button")
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Applica")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Salva Impostazioni".loc(), fontSize = 14.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
