package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.fintrack.mobile.ui.theme.components.MetaAlcanzadaModal
import pe.fintrack.mobile.ui.viewmodel.FormUiState
import pe.fintrack.mobile.ui.viewmodel.GoalFormUiState
import pe.fintrack.mobile.ui.viewmodel.GoalViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContribuirMetaScreen(
    goalId: Long,
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    // Estados para los campos
    var montoADepositar by remember { mutableStateOf("") }

    // Estados del ViewModel
    val formState by viewModel.formState.collectAsState()
    val goal by viewModel.selectedGoal.collectAsState()
    val goalAchieved by viewModel.goalAchievedEvent.collectAsState()

    // Formateador
    val currencyFormatter = remember {
        DecimalFormat("S/ #,##0.00", java.text.DecimalFormatSymbols(Locale("es", "PE")))
    }

    // Carga los detalles de la meta al entrar
    LaunchedEffect(key1 = goalId) {
        viewModel.loadGoalDetails(goalId)
    }

    // Resetea el estado al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFormState()
        }
    }

    // --- MODAL DE META ALCANZADA ---
    // Muestra el modal si 'goalAchieved' no es nulo
    goalAchieved?.let { achievedGoal ->
        MetaAlcanzadaModal(
            goal = achievedGoal,
            onDismiss = {
                viewModel.clearGoalAchievedEvent() // Limpia el evento
                onNavigateBack() // Navega hacia atrás
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Navegación "Volver" ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateBack),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Transferir a Meta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (formState == FormUiState.Loading && goal == null) {
            CircularProgressIndicator()
        } else if (goal != null){
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Campo: Nombre de la Meta (Read-only) ---
                    OutlinedTextField(
                        value = goal?.name ?: "Cargando meta...",
                        onValueChange = {},
                        label = { Text("Meta de Ahorro") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )

                    // --- Campo: Monto Ahorrado (Read-only) ---
                    OutlinedTextField(
                        value = currencyFormatter.format(goal!!.savedAmount ?: BigDecimal.ZERO),
                        onValueChange = {},
                        label = { Text("Monto ahorrado hasta ese momento") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )

                    // --- Campo: Monto a Depositar ---
                    OutlinedTextField(
                        value = montoADepositar,
                        onValueChange = { montoADepositar = it },
                        label = { Text("Monto a depositar") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = formState is GoalFormUiState.Error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Botón: Transferir ---
                    Button(
                        onClick = {
                            viewModel.addContribution(
                                amountStr = montoADepositar,
                                onSuccess = onNavigateBack
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF483D8B)),
                        enabled = formState != FormUiState.Loading
                    ) {
                        if (formState == FormUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Transferir")
                        }
                    }

                    // Mensaje de error (para validaciones)
                    if (formState is FormUiState.Error) {
                        Text(
                            text = (formState as FormUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        // Muestra un error si la API falló al cargar la meta
        else if (formState is GoalFormUiState.Error) {
            Text(
                text = (formState as GoalFormUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}