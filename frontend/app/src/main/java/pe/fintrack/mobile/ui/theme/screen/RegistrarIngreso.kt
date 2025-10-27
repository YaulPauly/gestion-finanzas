package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarIngresoScreen(onNavigateBack: () -> Unit) {
    // Estados para cada campo del formulario
    var titulo by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }
    val showDatePicker = remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Ingreso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica el padding del Scaffold
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // El título ahora está en la TopAppBar, por lo que se elimina de aquí.

            // Campo para el Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del movimiento") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el Monto
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto (S/.)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para la Fecha (abre un Date Picker)
            OutlinedTextField(
                value = fechaSeleccionada?.let { dateFormatter.format(it) } ?: "",
                onValueChange = { /* El valor se actualiza desde el DatePicker */ },
                label = { Text("Fecha") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker.value = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha"
                    )
                }
            )

            // Diálogo del Date Picker
            if (showDatePicker.value) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker.value = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    fechaSeleccionada = Date(it)
                                }
                                showDatePicker.value = false
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker.value = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

            // Botón para guardar
            Button(
                onClick = {
                    // TODO: Lógica para guardar el ingreso.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Ingreso")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarIngresoScreenPreview() {
    FintrackMobileTheme {
        RegistrarIngresoScreen(onNavigateBack = {}) // Se pasa una acción vacía para el preview
    }
}
