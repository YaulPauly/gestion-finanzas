package pe.fintrack.mobile.ui.theme.screen

// --- Imports de Compose ---
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Import para "Volver"
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults // Import para el icono del dropdown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.fintrack.mobile.ui.theme.data.Category

import pe.fintrack.mobile.ui.viewmodel.FormUiState
import pe.fintrack.mobile.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarIngresoScreen(
    transactionId: Long, // 1. Recibe el ID de la transacción
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    // Estados para los campos
    var monto by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var descripcion by remember { mutableStateOf("") }

    // Estados de UI
    var expanded by remember { mutableStateOf(false) } // Para el dropdown
    val categorias by viewModel.categories.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val transactionToEdit by viewModel.selectedTransaction.collectAsState()

    // 2. Carga los datos de la transacción al entrar
    LaunchedEffect(key1 = transactionId) {
        viewModel.loadTransactionDetails(transactionId)
        // (loadCategories se llama en el init del ViewModel)
    }

    // 3. Rellena el formulario cuando los datos de la transacción se cargan
    LaunchedEffect(key1 = transactionToEdit, key2 = categorias) {
        transactionToEdit?.let { transaction ->
            monto = transaction.amount.toPlainString() // Convierte BigDecimal a String
            descripcion = transaction.description ?: ""
            // Encuentra el objeto Category completo usando el categoryId
            selectedCategory = categorias.find { it.id == transaction.categoryId }
        }
    }

    // 4. Resetea el estado del formulario y la transacción al salir
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
                .clickable(onClick = onNavigateBack), // Usa la lambda para volver
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
            text = "Editar Ingreso", // Título cambiado
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
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") }, // Label
                    placeholder = { Text("0.00") }, // Placeholder
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Teclado numérico
                    singleLine = true,
                    isError = formState is FormUiState.Error // Marca en rojo si hay error
                )

                // --- Campo: Categoría (Dropdown) ---
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "", // Rellena con el valor cargado
                        onValueChange = { },
                        label = { Text("Categoría") },
                        placeholder = { Text("Selecciona una categoría") },
                        readOnly = true, // No se puede escribir
                        trailingIcon = { // Icono de flecha
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // Ancla el menú aquí
                        isError = formState is FormUiState.Error
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categorias.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // --- Campo: Descripción ---
                OutlinedTextField(
                    value = descripcion, // Rellena con el valor cargado
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Descripción (Opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp), // Altura para descripción
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Botón: Guardar Cambios ---
                Button(
                    onClick = {
                        // 5. Llama a UPDATE en lugar de CREATE
                        viewModel.updateIncome(
                            id = transactionId,
                            montoStr = monto,
                            categoryId = selectedCategory?.id,
                            description = descripcion,
                            onSuccess = onNavigateBack // Vuelve si tiene éxito
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = formState != FormUiState.Loading, // Deshabilita en carga
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF483D8B))
                ) {
                    if (formState == FormUiState.Loading) {
                        // Muestra el indicador de carga
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("Guardar Cambios") // Texto cambiado
                    }
                }

                // --- (Opcional) Botón Eliminar ---
                OutlinedButton(
                    onClick = {
                        // 6. Llama a DELETE (necesitas añadir 'deleteIncome' al ViewModel)
                        // viewModel.deleteIncome(transactionId, onNavigateBack)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Ingreso")
                }

                // Muestra mensaje de error si existe
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
}