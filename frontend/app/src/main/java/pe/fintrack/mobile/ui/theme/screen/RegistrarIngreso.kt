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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.viewmodel.FormUiState
import pe.fintrack.mobile.ui.viewmodel.TransactionViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarIngresoScreen(onNavigateBack: () -> Unit, viewModel: TransactionViewModel = viewModel()) {
    // Estados para los campos del formulario
    var monto by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) } // Almacena el objeto Category
    var descripcion by remember { mutableStateOf("") }

    // Estados para el UI
    var expanded by remember { mutableStateOf(false) }
    val categorias by viewModel.categories.collectAsState() // 2. Obtiene categorías del ViewModel
    val formState by viewModel.formState.collectAsState() // 3. Observa el estado del form

    // Resetea el estado cuando sales de la pantalla
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
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Título ---
        Text(
            text = "Registrar Ingreso",
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
                // --- Campo: Monto ---
                Text(
                    text = "Ingresar monto"
                )
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Ingresar monto") },
                    placeholder = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // --- Campo: Categoría (Dropdown) ---
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = { /* No se cambia directamente */ },
                        label = { Text("Ingresar categoria") },
                        placeholder = { Text("Categoria") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categorias.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    selectedCategory = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // --- Campo: Descripción ---
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Ingresar descripción") },
                    placeholder = { Text("Descripción (Opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp), // Altura para el campo de descripción
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Botón: Registrar ---
                Button(
                    onClick = {

                        viewModel.createIncome(
                            montoStr = monto,
                            categoryId = selectedCategory?.id,
                            description = descripcion,
                            onSuccess = onNavigateBack
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF483D8B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = formState != FormUiState.Loading // (Asegúrate de tener esto)
                ) {
                    if (formState == FormUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("Registrar")
                    }
                }
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