package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.viewmodel.BudgetViewModel
import com.example.ui.components.AddEditCategoryDialog
import com.example.ui.theme.*
import com.example.ui.loc

@Composable
fun CategoriesScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryForEdit by remember { mutableStateOf<Category?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Category?>(null) }
    var showLimitAlert by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SweetBackground)
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
                    text = "Gestione Categorie".loc(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = SweetTextDark
                )
                Text(
                    text = "Personalizza nomi e simboli di riferimento".loc(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SweetTextLight
                )
            }

            IconButton(
                onClick = {
                    if (categories.size >= 10) {
                        showLimitAlert = true
                    } else {
                        showAddDialog = true
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SweetPrimary)
                    .size(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuova Categoria".loc(),
                    tint = Color.White
                )
            }
        }

        // List Grid of Categories
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Tracciando categorie...".loc(), color = SweetTextLight)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { cat ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SweetSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Category Emoji Card
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SweetPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat.icon, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SweetTextDark,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    onClick = { selectedCategoryForEdit = cat },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifica".loc(),
                                        tint = SweetPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { showDeleteConfirmDialog = cat },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina".loc(),
                                        tint = PastelRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Create dialog
    if (showAddDialog) {
        AddEditCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, icon ->
                viewModel.addCategory(name, icon)
                showAddDialog = false
            }
        )
    }

    if (showLimitAlert) {
        AlertDialog(
            onDismissRequest = { showLimitAlert = false },
            title = { Text("Limite Categorie".loc(), fontWeight = FontWeight.Bold, color = SweetTextDark) },
            text = {
                Text(
                    "Puoi impostare al massimo 10 categorie per mantenere l'interfaccia pulita e leggibile. Se hai bisogno di una nuova categoria, modifica o elimina una di quelle esistenti.".loc(),
                    color = SweetTextLight
                )
            },
            confirmButton = {
                Button(
                    onClick = { showLimitAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SweetPrimary)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SweetSurface
        )
    }

    // Modal Edit dialog
    selectedCategoryForEdit?.let { cat ->
        AddEditCategoryDialog(
            categoryToEdit = cat,
            onDismiss = { selectedCategoryForEdit = null },
            onSave = { name, icon ->
                viewModel.updateCategory(cat.id, name, icon, cat.isDefault)
                selectedCategoryForEdit = null
            }
        )
    }

    // Modal Delete Confirmation
    showDeleteConfirmDialog?.let { cat ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminare categoria?".loc(), fontWeight = FontWeight.Bold, color = SweetTextDark) },
            text = {
                Text(
                    "Sicuro di voler eliminare la categoria '%s'? Le spese associate a questa categoria verranno ri-numerate come 'Generiche' ma non saranno rimosse dai conteggi totali del budget.".loc(cat.name),
                    color = SweetTextLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat)
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
