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
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarIngresoScreen(onNavigateBack: () -> Unit) {
    // Estados para cada campo del formulario
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf("Salario", "Ventas", "Bonos", "Regalo", "Otros") // Opciones de ejemplo

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
                        value = categoria,
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
                                text = { Text(item) },
                                onClick = {
                                    categoria = item
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
                        // TODO: Lógica para registrar el ingreso.
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF483D8B) // Color azul/púrpura oscuro
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrar")
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