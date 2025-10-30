package pe.fintrack.mobile.ui.theme.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.model.ExpenseRequest
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseActionState
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModel
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarGastoScreen(
    navController: NavController,
    expenseId: Long, // Recibe el ID del gasto a editar
    expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory (RetrofitClient.instance)
    )
) {
    val context = LocalContext.current

    // --- ESTADOS LOCALES ---
    var initialLoadComplete by remember { mutableStateOf(false) }
    var currentTransaction by remember { mutableStateOf<Transaction?>(null) }

    // Estados del formulario
    var amountInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    // Estados del ViewModel
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    val categories by expenseViewModel.categoryState.collectAsState()
    val actionState by expenseViewModel.expenseActionState.collectAsState()
    val isLoading = actionState is ExpenseActionState.Loading

    // 1. Carga los detalles del gasto al iniciar la pantalla
    LaunchedEffect(expenseId) {
        if (!initialLoadComplete) {
            val transaction = expenseViewModel.getExpenseById(expenseId)

            if (transaction != null) {
                currentTransaction = transaction
                // Rellena el formulario
                amountInput = transaction.amount.toPlainString()
                descriptionInput = transaction.description ?: ""

                // Encontrar y establecer la categoría inicial (requiere que 'categories' ya esté cargada)
                // Usamos LaunchedEffect separado para esperar por categories si es necesario
                // Pero lo haremos en el siguiente LaunchedEffect para simplificar.

            } else {
                Toast.makeText(context, "Error al cargar el gasto.", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    }

    // 2. Rellena la Categoría una vez que tanto el Gasto como la lista de Categorías estén listos
    LaunchedEffect(currentTransaction, categories) {
        if (currentTransaction != null && categories.isNotEmpty()) {
            selectedCategory = categories.find { it.id == currentTransaction!!.categoryId }
            initialLoadComplete = true
        }
    }

    // 3. Manejo de la acción de actualización
    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is ExpenseActionState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                expenseViewModel.resetActionState()
                navController.popBackStack() // Vuelve a la lista
            }
            is ExpenseActionState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                // No volvemos, permitimos al usuario intentar de nuevo
                expenseViewModel.resetActionState()
            }
            else -> {}
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Editar Gasto") },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        // Muestra indicador de carga mientras se cargan los datos iniciales
        if (!initialLoadComplete || isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // --- Contenido del Formulario ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Detalles del Gasto",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // --- Campo: Monto ---
                    Text(text = "Monto", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            amountInput = it.filter { char -> char.isDigit() || char == '.' }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Campo: Categoría (Dropdown) ---
                    Text(text = "Categoría", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Selecciona una categoría",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (isCategoryDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    "Expandir/Colapsar",
                                    Modifier.clickable { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
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

                    // --- Campo: Descripción ---
                    Text(text = "Descripción", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        placeholder = { Text("Descripción (Opcional)") },
                        singleLine = false,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Botón: Guardar Cambios ---
                    Button(
                        onClick = {
                            val amount = amountInput.toBigDecimalOrNull()
                            val categoryId = selectedCategory?.id

                            if (amount == null || amount <= 0.toBigDecimal() || categoryId == null) {
                                Toast.makeText(context, "Monto y categoría son obligatorios.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val request = ExpenseRequest(
                                amount = amount,
                                categoryId = categoryId,
                                description = descriptionInput.ifBlank { null }
                            )

                            // 4. Llama a la función de ACTUALIZACIÓN con el ID
                            expenseViewModel.updateExpense(expenseId, request)
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Guardar Cambios")
                        }
                    }

                    // --- Opcional: Botón Eliminar ---
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            // TODO: Implementar lógica de eliminación y confirmación en el ViewModel
                            Toast.makeText(context, "Eliminar Gasto ID: $expenseId (TODO)", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar Gasto")
                    }
                }
            }
        }
    }
}