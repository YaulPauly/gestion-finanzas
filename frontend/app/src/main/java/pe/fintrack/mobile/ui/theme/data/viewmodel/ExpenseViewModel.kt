package pe.fintrack.mobile.ui.theme.data.viewmodel

import android.util.Log
import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.model.ExpenseRequest
import pe.fintrack.mobile.ui.theme.data.network.ApiService
import java.io.IOException

class ExpenseViewModel(
    // El ApiService se recibe como un parámetro regular
    private val apiService: ApiService
) : ViewModel() {

    // --- Estado para la Lista de Gastos (ReadOnly) ---
    private val _expenseListState = MutableStateFlow(ExpenseListUiState())
    val expenseListState: StateFlow<ExpenseListUiState> = _expenseListState.asStateFlow()

    // --- Nuevo Estado para Categorías ---
    private val _categoryState = MutableStateFlow<List<Category>>(emptyList())
    val categoryState: StateFlow<List<Category>> = _categoryState.asStateFlow()

    // --- Opcional: Estado para registrar/editar un gasto ---
    // (Útil para saber si la operación fue exitosa)
    private val _expenseActionState = MutableStateFlow<ExpenseActionState>(ExpenseActionState.Idle)
    val expenseActionState: StateFlow<ExpenseActionState> = _expenseActionState.asStateFlow()

    init {
        // Carga inicial de gastos al crear el ViewModel
        loadExpenses()
        loadCategories()
    }

    // ===================================================================
    // 1. OBTENER GASTOS (READ)
    // ===================================================================

    fun loadExpenses(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            // 1. Iniciar carga
            _expenseListState.value = _expenseListState.value.copy(
                isLoading = true,
                error = null,
                currentPage = page // Actualizar la página actual
            )

            try {
                // 2. Llamada a la API
                val response = apiService.getExpenses(page, size)

                // 3. Manejo de respuesta HTTP (aunque Retrofit maneja la mayoría de errores)
                if (response.isSuccessful && response.body() != null) {
                    val pagedResult = response.body()!!

                    // Si es la primera página, reemplazamos, si no, añadimos (Paginación)
                    val newExpenses = if (page == 0) {
                        pagedResult.content // 'content' de PagedResult de Spring Boot
                    } else {
                        _expenseListState.value.expenses + pagedResult.content
                    }

                    // 4. Actualizar estado de éxito
                    _expenseListState.value = _expenseListState.value.copy(
                        isLoading = false,
                        expenses = newExpenses,
                        hasMorePages = !pagedResult.last, // 'last' del PagedResult de Spring Boot
                        currentPage = pagedResult.number
                    )
                } else {
                    // Manejo de errores 4xx/5xx del servidor
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _expenseListState.value = _expenseListState.value.copy(
                        isLoading = false,
                        error = "Error al cargar gastos: ${response.code()} - $errorBody"
                    )
                }
            } catch (e: HttpException) {
                // Errores de red (ej: 401 Unauthorized, 404 Not Found)
                _expenseListState.value = _expenseListState.value.copy(
                    isLoading = false,
                    error = "Error HTTP: ${e.code()}.No se pudo contactar al servidor."
                )
            } catch (e: IOException) {
                // Errores de conexión (ej: sin internet, timeout)
                _expenseListState.value = _expenseListState.value.copy(
                    isLoading = false,
                    error = "Error de conexión. Revisa tu internet."
                )
            } catch (e: Exception) {
                // Otros errores
                _expenseListState.value = _expenseListState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.localizedMessage}"
                )
            }
        }
    }

    // ===================================================================
    // 2. REGISTRAR GASTO (CREATE)
    // ===================================================================

    fun createExpense(request: ExpenseRequest) {
        viewModelScope.launch {
            _expenseActionState.value = ExpenseActionState.Loading
            try {
                val response = apiService.createExpense(request)

                if (response.isSuccessful && response.body() != null) {
                    // Éxito: Notificar a la UI y recargar la lista de gastos
                    _expenseActionState.value = ExpenseActionState.Success("Gasto registrado con éxito")
                    loadExpenses(page = 0) // Recargar la primera página para ver el nuevo gasto
                } else {
                    val error = response.errorBody()?.string() ?: "Error de registro desconocido."
                    _expenseActionState.value = ExpenseActionState.Error(error)
                }
            } catch (e: Exception) {
                _expenseActionState.value = ExpenseActionState.Error("Fallo al registrar: ${e.localizedMessage}")
            }
        }
    }

    // Función de ayuda para notificar a la UI que ya puede reaccionar a otra acción
    fun resetActionState() {
        _expenseActionState.value = ExpenseActionState.Idle
    }

    // ===================================================================
    // 3. PAGINACIÓN
    // ===================================================================

    fun loadNextPage() {
        // Solo carga si no está ya cargando y si hay más páginas disponibles
        if (!_expenseListState.value.isLoading && _expenseListState.value.hasMorePages) {
            val nextPage = _expenseListState.value.currentPage + 1
            loadExpenses(page = nextPage)
        }
    }

    // ===================================================================
    // 4. OBTENER CATEGORÍAS
    // ===================================================================

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categoryState.value = response.body()!!
                } else {
                    // Manejar error de carga de categorías, quizás loguear
                    Log.e("ExpenseViewModel", "Error al cargar categorías: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ExpenseViewModel", "Fallo de conexión al cargar categorías", e)
            }
        }
    }
}


// --- Estado para acciones individuales (Crear/Editar/Eliminar) ---
// Define todos los estados de una operación de red
sealed class ExpenseActionState {
    data object Idle : ExpenseActionState()
    data object Loading : ExpenseActionState()
    data class Success(val message: String) : ExpenseActionState()
    data class Error(val message: String) : ExpenseActionState()
}