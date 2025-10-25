package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.components.FinTrackTopBar
import pe.fintrack.mobile.ui.theme.components.SaldoActualComponent
import pe.fintrack.mobile.ui.viewmodel.HomeViewModel
import java.util.Locale
import pe.fintrack.mobile.data.model.HomeUiState
import pe.fintrack.mobile.ui.theme.components.BottomNavigationBar


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val state by viewModel.uiState.collectAsState()

    // USAMOS SCAFFOLD EN EL NIVEL SUPERIOR
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FinTrackTopBar(
                nombreUsuario = state.nombreUsuario,
                onNotificationClick = { /* Implementar lógica de notificaciones */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        // 2. LLAMAMOS A HOMECONTENT DENTRO DEL SCAFFOLD, APLICANDO EL PADDING
        HomeContent(
            state = state,
            navController = navController,
            // Aplicar el padding del Scaffold al contenido
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Opcional: Mostrar error y carga
    if (state.errorMessage != null) { /* ... */ }
    if (state.isLoading) { /* ... */ }
}


@Composable
    fun HomeContent(
        state: HomeUiState,
        navController: NavController,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
        ) {
            // --- SALDO ACTUAL ---
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF4A55A2))
            ) {
                SaldoActualComponent(
                    saldoActual = state.saldoActual,
                    onRegistrarGastoClick = { navController.navigate(AppScreen.RegistrarGastos.route) },
                    onRegistrarIngresoClick = { navController.navigate(AppScreen.RegistrarIngreso.route) }
                )
            }

            // --- RESUMEN MENSUAL ---
            Text(
                text = "Resumen Mensual",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResumenCard(
                    titulo = "Ingresos",
                    monto = state.resumen.ingresos,
                    colorFondo = Color(0xFF4A55A2),
                    colorTexto = Color.White,
                    esIngreso = true,
                    modifier = Modifier.weight(1f)
                )
                ResumenCard(
                    titulo = "Gastos",
                    monto = state.resumen.gastos,
                    colorFondo = Color.White,
                    colorTexto = Color.Black,
                    esIngreso = false,
                    modifier = Modifier.weight(1f)
                )
            }

            // --- MOVIMIENTOS RECIENTES ---
            Text(
                text = "Movimientos recientes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn {
                    items(state.movimientosRecientes) { movimiento ->
                        MovimientoItem(
                            categoria = movimiento.categoria,
                            fecha = movimiento.fecha,
                            monto = if (movimiento.esIngreso) "+ S/. ${String.format(Locale.US, "%,.2f", movimiento.monto)}" else "- S/. ${String.format(Locale.US, "%,.2f", movimiento.monto)}",
                            esIngreso = movimiento.esIngreso
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
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
fun GastosScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Gastos!")
}

@Composable
fun MovimientosScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Movimientos!")
}
