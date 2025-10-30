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
import pe.fintrack.mobile.ui.viewmodel.GoalFormUiState
import pe.fintrack.mobile.ui.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMetaScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    // Estados para los campos
    var nombre by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val formState by viewModel.formState.collectAsState()

    // Resetea el estado al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFormState()
        }
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
            text = "Crear Nueva Meta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Tarjeta del Formulario ---
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
                // --- Campo: Nombre ---
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la meta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = formState is GoalFormUiState.Error
                )

                // --- Campo: Monto Objetivo ---
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Monto Objetivo") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = formState is GoalFormUiState.Error
                )

                // --- Campo: Descripción ---
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (Opcional)") },
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Botón: Crear Meta ---
                Button(
                    onClick = {
                        viewModel.createGoal(
                            nombre = nombre,
                            objetivoStr = objetivo,
                            descripcion = descripcion,
                            onSuccess = onNavigateBack
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF483D8B)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = formState != GoalFormUiState.Loading
                ) {
                    if (formState == GoalFormUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Crear Meta")
                    }
                }

                // Mensaje de error
                if (formState is GoalFormUiState.Error) {
                    Text(
                        text = (formState as GoalFormUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}