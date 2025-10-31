package pe.fintrack.mobile.ui.theme.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.model.ExpenseRequest
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseActionState
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModel
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModelFactory
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarGastoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(RetrofitClient.instance)
    )
) {
    // 1. OBTENCIÓN DEL VIEWMODEL Y ESTADOS
    val apiService = remember { RetrofitClient.instance }
    val factory = remember { ExpenseViewModelFactory(apiService) }
    val expenseViewModel: ExpenseViewModel = viewModel (factory = factory)

    val actionState by expenseViewModel.expenseActionState.collectAsState()
    val categories by expenseViewModel.categoryState.collectAsState()
    val context = LocalContext.current

    // --- ESTADO LOCAL DEL FORMULARIO ---
    var amountInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // Estado de carga y errores
    val isLoading = actionState is ExpenseActionState.Loading

    // 2. MANEJO DE ACCIÓN DE RED (Éxito o Error)
    LaunchedEffect (actionState) {
        when (val state = actionState) {
            is ExpenseActionState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                expenseViewModel.resetActionState()
                navController.popBackStack()
            }
            is ExpenseActionState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                expenseViewModel.resetActionState()
            }
            else -> {}
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Registrar Gasto") },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                // El padding superior es dado por el Scaffold, usamos el resto
                .padding(paddingValues)
                .background(Color(0xFFF0F0F0)) // Fondo suave (similar al de ListaGastos)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // --- TARJETA DE FORMULARIO ---
            Card (
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Registrar Gasto",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // 1. CAMPO MONTO
                    Text(text = "Ingresar monto", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            amountInput = it.filter { char -> char.isDigit() || char == '.' }
                        },
                        placeholder = { Text("Monto") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. CAMPO CATEGORÍA (Dropdown)
                    Text(text = "Ingresar categoría", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Categoría",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (isCategoryDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    "Expandir/Colapsar",
                                    Modifier.clickable {
                                        isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                                        // Opcional: mostrar un Toast si la lista de categorías está vacía
                                        if (categories.isEmpty() && !isCategoryDropdownExpanded) {
                                            expenseViewModel.loadCategories() // Intentar recargar
                                            Toast.makeText(context, "Cargando categorías...", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF0F0F0)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
                        )

                        DropdownMenu (
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            if (categories.isEmpty()) {
                                DropdownMenuItem(text = { Text("Cargando categorías o lista vacía...") }, onClick = {})
                            }
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. CAMPO DESCRIPCIÓN
                    Text(text = "Ingresar descripción", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        placeholder = { Text("Descripción (Opcional)") },
                        singleLine = false,
                        maxLines = 4, // Permite varias líneas
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp) // Altura mínima para el área de texto
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. BOTÓN DE REGISTRAR
                    Button (
                        onClick = {
                            val amount = amountInput.toBigDecimalOrNull()
                            val categoryId = selectedCategory?.id

                            if (amount == null || amount <= BigDecimal.ZERO || categoryId == null) {
                                Toast.makeText(context, "Completa monto y categoría.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val request = ExpenseRequest(
                                amount = amount,
                                categoryId = categoryId,
                                description = descriptionInput.ifBlank { null }
                            )

                            expenseViewModel.createExpense(request)
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(25.dp), // Esquinas redondeadas
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Botón un poco más estrecho
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrar")
                        }
                    }
                }
            }
        }
    }
}