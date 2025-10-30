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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // Formateador de moneda
    val currencyFormatter = remember {
        DecimalFormat("S/ #,##0.00", java.text.DecimalFormatSymbols(Locale("es", "PE")))
    }

    // Formateadores de fecha
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    movementViewModel.downloadReport(context)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Generar Reporte",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(innerPadding) // <-- 5. Aplicamos el padding aquí
                .padding(horizontal = 16.dp) // Mantenemos tu padding horizontal
        ) {
            // Título
            Text(
                text = "Actividad",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            //
            // Box(
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .padding(16.dp),
            //     contentAlignment = Alignment.Center
            // ){
            //     CircleActionButton(
            //         text = "Generar\nReporte",
            //         icon = Icons.Default.Email,
            //         onClick = { /* TODO: Lógica para reporte */ }
            //     )
            // }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is MovimientoListaUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MovimientoListaUiState.Success -> {
                    if (state.movements.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron movimientos.")
                        }
                    } else {


                        val categoryMap = remember(state.categories) {
                            state.categories.associateBy { it.id }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.movements,
                                key = { transaction -> transaction.id }
                            ) { transaction: Transaction ->
                                val esIngreso = transaction.type == TransactionType.INCOME

                                val categoryName = categoryMap[transaction.categoryId]?.name ?: "Movimiento"

                                val formattedDate = try {
                                    LocalDate.parse(transaction.date, inputFormatter).format(outputFormatter)
                                } catch (e: Exception) {
                                    transaction.date
                                }

                                MovimientoItem(
                                    categoria = categoryName,
                                    fecha = formattedDate,
                                    monto = "${if (esIngreso) "+" else "-"} ${currencyFormatter.format(transaction.amount)}",
                                    esIngreso = esIngreso,
                                    modifier = Modifier.clickable {

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
                is MovimientoListaUiState.Error -> {
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
}