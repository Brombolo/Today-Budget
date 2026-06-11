package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    if (onboardingCompleted == null) {
        // Still loading settings, show a simple splash/empty or nothing
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator(color = SweetPrimary)
        }
    } else if (onboardingCompleted == false) {
        OnboardingScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = SweetSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Oggi".loc()) },
                        label = { Text("Oggi".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SweetPrimary,
                            selectedTextColor = SweetPrimary,
                            indicatorColor = SweetPrimaryLight,
                            unselectedIconColor = SweetTextLight,
                            unselectedTextColor = SweetTextLight
                        ),
                        modifier = Modifier.testTag("nav_oggi_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(imageVector = Icons.Default.History, contentDescription = "Cronologia".loc()) },
                        label = { Text("Cronologia".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SweetPrimary,
                            selectedTextColor = SweetPrimary,
                            indicatorColor = SweetPrimaryLight,
                            unselectedIconColor = SweetTextLight,
                            unselectedTextColor = SweetTextLight
                        ),
                        modifier = Modifier.testTag("nav_cronologia_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(imageVector = Icons.Default.BarChart, contentDescription = "Statistiche".loc()) },
                        label = { Text("Analisi".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SweetPrimary,
                            selectedTextColor = SweetPrimary,
                            indicatorColor = SweetPrimaryLight,
                            unselectedIconColor = SweetTextLight,
                            unselectedTextColor = SweetTextLight
                        ),
                        modifier = Modifier.testTag("nav_analisi_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(imageVector = Icons.Default.Category, contentDescription = "Categorie".loc()) },
                        label = { Text("Categorie".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SweetPrimary,
                            selectedTextColor = SweetPrimary,
                            indicatorColor = SweetPrimaryLight,
                            unselectedIconColor = SweetTextLight,
                            unselectedTextColor = SweetTextLight
                        ),
                        modifier = Modifier.testTag("nav_categorie_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Impostazioni".loc()) },
                        label = { Text("Opzioni".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SweetPrimary,
                            selectedTextColor = SweetPrimary,
                            indicatorColor = SweetPrimaryLight,
                            unselectedIconColor = SweetTextLight,
                            unselectedTextColor = SweetTextLight
                        ),
                        modifier = Modifier.testTag("nav_opzioni_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { selectedTab = it }
                    )
                    1 -> HistoryScreen(viewModel = viewModel)
                    2 -> StatsScreen(viewModel = viewModel)
                    3 -> CategoriesScreen(viewModel = viewModel)
                    4 -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
