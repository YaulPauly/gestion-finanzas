package pe.fintrack.mobile.ui.theme.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.TransactionType
import pe.fintrack.mobile.ui.theme.data.viewmodel.MovimientoListaUiState
import pe.fintrack.mobile.ui.theme.data.viewmodel.MovimientoViewModel
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MovimientosScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    movementViewModel: MovimientoViewModel = viewModel()
) {
    val uiState by movementViewModel.uiState.collectAsState()

    // Formateador de moneda
    val currencyFormatter = remember {
        DecimalFormat("S/ #,##0.00", java.text.DecimalFormatSymbols(Locale("es", "PE")))
    }

    // Formateadores de fecha (para convertir "YYYY-MM-DD" a "DD/MM/YYYY")
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 16.dp)
    ) {
        // Título
        Text(
            text = "Movimientos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            CircleActionButton(
                text = "Generar\nReporte",
                icon = Icons.Default.Email,
                onClick = { /* TODO: Lógica para reporte */ }
            )
        }



        Spacer(modifier = Modifier.height(24.dp))

        // Contenido de la lista
        when (val state = uiState) {
            is MovimientoListaUiState.Loading -> { // Estado corregido
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MovimientoListaUiState.Success -> { // Estado corregido
                if (state.movements.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron movimientos.")
                    }
                } else {

                    // 1. Crea un mapa de búsqueda (ID -> Nombre)
                    val categoryMap = remember(state.categories) {
                        state.categories.associateBy { it.id }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.movements,
                            key = { transaction -> transaction.id } // Clave única
                        ) { transaction: Transaction ->
                            val esIngreso = transaction.type == TransactionType.INCOME

                            // 2. Busca el nombre de la categoría en el mapa
                            val categoryName = categoryMap[transaction.categoryId]?.name ?: "Movimiento"

                            // 3. Parsea y formatea la fecha
                            val formattedDate = try {
                                LocalDate.parse(transaction.date, inputFormatter).format(outputFormatter)
                            } catch (e: Exception) {
                                transaction.date // Fallback si el formato es incorrecto
                            }

                            MovimientoItem(
                                categoria = categoryName, // <-- CORREGIDO
                                fecha = formattedDate, // <-- CORREGIDO
                                monto = "${if (esIngreso) "+" else "-"} ${currencyFormatter.format(transaction.amount)}",
                                esIngreso = esIngreso,
                                modifier = Modifier.clickable {
                                    // Navega a la pantalla de edición correcta
                                    val route = if (esIngreso) {
                                        AppScreen.EditarIngreso.createRoute(transaction.id)
                                    } else {
                                        AppScreen.EditarGastos.createRoute(transaction.id)
                                    }
                                    navController.navigate(route)
                                }
                            )
                        }
                    }
                }
            }
            is MovimientoListaUiState.Error -> { // Estado corregido
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}