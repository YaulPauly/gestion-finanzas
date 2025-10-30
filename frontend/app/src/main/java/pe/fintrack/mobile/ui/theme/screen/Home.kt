package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.components.FinTrackTopBar
import pe.fintrack.mobile.ui.theme.components.SaldoActualComponent
import pe.fintrack.mobile.ui.theme.data.TokenManager
import pe.fintrack.mobile.ui.theme.data.TransactionType
import pe.fintrack.mobile.ui.theme.data.viewmodel.DashboardUiState
import pe.fintrack.mobile.ui.theme.data.viewmodel.HomeViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier, homeViewModel: HomeViewModel = viewModel()) {
    // estado de viewmodel
    val summaryState by homeViewModel.summaryState.collectAsState()
    // Formatear monedas
    val currencyFormatter = remember { DecimalFormat("S/ #,##0.00",
        DecimalFormatSymbols(Locale("es","PE"))
    )}

    val nombreUsuario = remember { TokenManager.getUserName() ?: "Usuario" }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        FinTrackTopBar(
            nombreUsuario = nombreUsuario,
            onCerrarSesionClick = {
                TokenManager.clearSession()
                navController.navigate(AppScreen.Login.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onNotificationClick = {
                // TODO: Lógica para el botón de notificaciones
            }
        )
        // Usa un 'when' para reaccionar al estado
        when (val state = summaryState) {
            is DashboardUiState.Loading -> {
                // Muestra un indicador de carga centrado
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Success -> {
                // --- Muestra los datos cuando la carga es exitosa ---
                val summary = state.summary


                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF4A55A2))
                ) {
                    // Pasa el saldo del ViewModel
                    SaldoActualComponent(
                        saldoActual = summary.currentBalance.toDouble(),
                        onRegistrarGastoClick = { navController.navigate(AppScreen.RegistrarGastos.route) },
                        onRegistrarIngresoClick = { navController.navigate(AppScreen.RegistrarIngreso.route) }
                    )
                }

                // --- Resumen Mensual ---
                Text(
                    text = "Resumen Mensual", /* ... */
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResumenCard(
                        titulo = "Ingresos",
                        monto = summary.monthlyIncome.toDouble(),
                        esIngreso = true,
                        colorFondo = Color(0xFFF3C467B),
                        colorTexto = Color(0xFFFFFFFFF),
                        modifier = Modifier.weight(1f)
                    )
                    ResumenCard(
                        titulo = "Gastos",
                        monto = summary.monthlyExpense.toDouble(), // Convertir o formatear
                        colorFondo = Color(0xFFFB8B8B8),
                        colorTexto = Color(0xFFFFFFFFF),
                        modifier = Modifier.weight(1f),
                        esIngreso = false
                    )
                }

                // --- Movimientos Recientes ---
                Text(
                    text = "Movimientos recientes", /* ... */
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1f), /* ... */
                ) {
                    // Muestra la lista de transacciones recientes del ViewModel
                    if (summary.recentTransactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No hay movimientos recientes")
                        }
                    } else {
                        LazyColumn {
                            items(summary.recentTransactions.size) { index ->
                                val transaction = summary.recentTransactions[index]
                                MovimientoItem(
                                    // Busca el nombre de la categoría si lo tienes, sino usa la descripción
                                    categoria = transaction.description ?: "Movimiento",
                                    // Formatea la fecha si es necesario
                                    fecha = transaction.date, // Podrías querer parsear y formatear esto
                                    // Formatea el monto con el símbolo correcto
                                    monto = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} ${currencyFormatter.format(transaction.amount)}",
                                    esIngreso = transaction.type == TransactionType.INCOME,
                                    // Añade el click listener
                                    modifier = Modifier.clickable() {
                                        // Navega a la pantalla de edición correspondiente
                                        val route = if (transaction.type == TransactionType.INCOME) {
                                            AppScreen.EditarIngreso.route + "/${transaction.id}" // Pasa el ID
                                        } else {
                                            AppScreen.EditarGastos.route + "/${transaction.id}" // Pasa el ID
                                        }
                                        navController.navigate(route)
                                    }
                                )
                                // Añade Divider si no es el último elemento
                                if (index < summary.recentTransactions.size - 1) {
                                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
            is DashboardUiState.Error -> {
                // Muestra un mensaje de error centrado
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Error al cargar: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    // Podrías añadir un botón para reintentar
                    Button(onClick = { homeViewModel.loadDashboardSummary() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenCard(
    titulo: String,
    monto: Double,
    colorFondo: Color,
    colorTexto: Color,
    esIngreso: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = titulo, color = colorTexto, style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (esIngreso) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = titulo,
                    tint = colorTexto
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "S/. ${String.format(Locale.US, "%,.2f", monto)}",
                color = colorTexto,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MovimientoItem(
    categoria: String,
    fecha: String,
    monto: String,
    esIngreso: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = categoria, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = fecha, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = monto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}


@Composable
fun IngresoScreen(navController: NavController, modifier: Modifier = Modifier) {
    ListaIngresosScreen(navController = navController)
}

@Composable
fun GastosScreen(navController: NavController,modifier: Modifier = Modifier) {
    ListaGastosScreen(navController = navController)
}

@Composable
fun MovimientosScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Movimientos!")
}