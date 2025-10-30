package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.TransactionType
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModel
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListaGastosScreen(
    navController: NavController,
    modifier: Modifier = Modifier
    // expenseViewModel: ExpenseViewModel = viewModel() // <-- Así inyectarías el ViewModel
) {
    // --- ESTADO (Ejemplo de cómo sería con ViewModel) ---
    // val expenseState by expenseViewModel.expenseListState.collectAsState()
    // Aquí usamos datos de ejemplo por ahora, pero usando el modelo Transaction
    val apiService = remember { RetrofitClient.instance }// Obtener la instancia
    val factory = remember { ExpenseViewModelFactory(apiService) }
    val expenseViewModel: ExpenseViewModel = viewModel (factory = factory)

// 2. CONSUMO DEL ESTADO DEL VIEWMODEL
    // **********************************************
    val expenseState by expenseViewModel.expenseListState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 16.dp)
    ) {
        // --- CABECERA Y BOTONES DE ACCIÓN (Sin cambios) ---
        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            CircleActionButton(
                text = "Registrar\ngastos",
                icon = Icons.Default.Add,
                onClick = {
                    navController.navigate(AppScreen.RegistrarGastos.route)
                }
            )
            CircleActionButton(
                text = "Generar\nReporte",
                icon = Icons.Default.Email,
                onClick = { /* TODO: Lógica para reporte */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. RENDERIZADO CONDICIONAL DE LA LISTA SEGÚN EL ESTADO
        // **********************************************
        when {
            // Muestra indicador de carga mientras se obtienen los datos
            expenseState.isLoading && expenseState.expenses.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Muestra error si existe y la lista está vacía
            expenseState.error != null && expenseState.expenses.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = expenseState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                    Button(onClick = { expenseViewModel.loadExpenses() }) {
                        Text("Reintentar Carga")
                    }
                }
            }

            // Muestra la lista de gastos
            expenseState.expenses.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expenseState.expenses) { gasto ->
                        GastosItem(
                            gasto = gasto,
                            modifier = Modifier.clickable {
                                navController.navigate(AppScreen.EditarGastos.route + "/${gasto.id}")
                            }
                        )
                    }

                    // Lógica para el cargador de la siguiente página (paginación)
                    item {
                        if (expenseState.hasMorePages) {
                            // Se llama al ViewModel para cargar la siguiente página
                            // Este es un buen lugar para implementar el "scroll infinito"
                            // Por ahora, solo ponemos el indicador de carga
                            LaunchedEffect (true) {
                                expenseViewModel.loadNextPage()
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(Modifier.size(30.dp))
                            }
                        }
                    }
                }
            }

            // Muestra mensaje si la lista está vacía y no hay errores ni carga
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes gastos registrados. ¡Empieza a registrar!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CircleActionButton(
    onClick: () -> Unit,
    buttonText: String,
    contentDescription: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 64.dp,
    iconSize: Dp = 32.dp,
    circleColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        modifier = modifier.clickable(
            onClick = onClick,
            role = Role.Button,
            onClickLabel = contentDescription
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = buttonText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun GastosItem(gasto: Transaction, modifier: Modifier = Modifier) {
    // Formateador de moneda
    val currencyFormatter =
        remember { DecimalFormat("S/ #,##0.00", java.text.DecimalFormatSymbols(Locale("es", "PE"))) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gasto.description ?: "Gasto", // Usa la descripción
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = gasto.date, // Usa la fecha (puedes formatearla)
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Text(
                // Usa el 'amount' y formatea
                text = "- ${currencyFormatter.format(gasto.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF821E1E) // Rojo para gastos
            )
        }
    }
}