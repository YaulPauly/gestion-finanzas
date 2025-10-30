package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.data.Goal
import pe.fintrack.mobile.ui.viewmodel.GoalListUiState
import pe.fintrack.mobile.ui.viewmodel.GoalViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

@Composable
fun ListaMetasScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.listState.collectAsState()

    Scaffold(
        floatingActionButton = {
            // Botón para "Crear nueva meta de ahorro"
            FloatingActionButton(
                onClick = { navController.navigate(AppScreen.CrearMeta.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Meta", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Título
            Text(
                text = "Metas de Ahorro",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido de la lista
            when (val state = uiState) {
                is GoalListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is GoalListUiState.Success -> {
                    if (state.goals.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aún no tienes metas. ¡Crea una!")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.goals) { goal ->
                                // --- HACE EL ITEM CLICABLE ---
                                GoalItem(goal = goal, modifier = Modifier.clickable {
                                    // Navega a la pantalla de contribución
                                    navController.navigate(AppScreen.ContribuirMeta.createRoute(goal.id))
                                })
                            }
                        }
                    }
                }
                is GoalListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalItem(goal: Goal, modifier: Modifier = Modifier) {
    val currencyFormatter = remember {
        DecimalFormat("S/ #,##0.00", java.text.DecimalFormatSymbols(Locale("es", "PE")))
    }

    val savedAmount = goal.savedAmount ?: BigDecimal.ZERO

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = goal.description ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            val progress = if (goal.target > BigDecimal.ZERO) {
                (savedAmount.divide(goal.target, 2, BigDecimal.ROUND_HALF_UP)).toFloat().coerceIn(0f, 1f)
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)), // Bordes redondeados
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Textos de progreso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = currencyFormatter.format(savedAmount),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Objetivo: ${currencyFormatter.format(goal.target)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}