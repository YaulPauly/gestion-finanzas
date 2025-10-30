package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import pe.fintrack.mobile.ui.theme.data.Category
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.model.IncomeRequest
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.io.IOException
import java.math.BigDecimal


sealed class FormUiState {
    object Idle : FormUiState() // Estado inicial
    object Loading : FormUiState() // Guardando/Cargando
    object Success : FormUiState() // Éxito al guardar
    data class Error(val message: String) : FormUiState() // Error
}

class TransactionViewModel : ViewModel() {

    // --- Para el Dropdown de Categorías ---
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // --- Para la pantalla de Editar (carga los datos) ---
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    // --- Para el estado del formulario (Loading, Success, Error) ---
    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Idle)
    val formState: StateFlow<FormUiState> = _formState.asStateFlow()

    init {
        loadCategories() // Carga las categorías al iniciar
    }

    // 1. Carga las categorías para el dropdown
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categories.value = response.body()!!
                }
            } catch (e: IOException) {
                // Manejar error de red (ej. en un StateFlow de error)
            }
        }
    }

    // 2. Crea un nuevo ingreso
    fun createIncome(montoStr: String, categoryId: Int?, description: String?, onSuccess: () -> Unit) {
        // Validación básica
        if (montoStr.isBlank() || categoryId == null) {
            _formState.value = FormUiState.Error("Monto y categoría son obligatorios.")
            return
        }
        val amount = montoStr.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _formState.value = FormUiState.Error("Monto inválido.")
            return
        }

        viewModelScope.launch {
            _formState.value = FormUiState.Loading
            try {
                val request = IncomeRequest(amount, categoryId, description.takeIf { !it.isNullOrBlank() })
                val response = RetrofitClient.instance.createIncome(request)

                if (response.isSuccessful) {
                    _formState.value = FormUiState.Success
                    onSuccess() // Navega hacia atrás
                } else {
                    _formState.value = FormUiState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // 3. Carga datos de un ingreso existente (para Editar)
    fun loadTransactionDetails(id: Long) {
        viewModelScope.launch {
            _formState.value = FormUiState.Loading
            try {

                val response = RetrofitClient.instance.getTransactionById(id)
                if (response.isSuccessful && response.body() != null) {
                    _selectedTransaction.value = response.body()
                    _formState.value = FormUiState.Idle // Listo para editar
                } else {
                    _formState.value = FormUiState.Error("No se pudo cargar la transacción")
                }
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // 4. Actualiza un ingreso existente
    fun updateIncome(id: Long, montoStr: String, categoryId: Int?, description: String?, onSuccess: () -> Unit) {
        // Validación (similar a create)
        if (montoStr.isBlank() || categoryId == null) {
            _formState.value = FormUiState.Error("Monto y categoría son obligatorios.")
            return
        }
        val amount = montoStr.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _formState.value = FormUiState.Error("Monto inválido.")
            return
        }

        viewModelScope.launch {
            _formState.value = FormUiState.Loading
            try {
                val request = IncomeRequest(amount, categoryId, description.takeIf { !it.isNullOrBlank() })
                val response = RetrofitClient.instance.updateIncome(id, request) // Llama a update

                if (response.isSuccessful) {
                    _formState.value = FormUiState.Success
                    onSuccess() // Navega hacia atrás
                } else {
                    _formState.value = FormUiState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // 5. Resetea el estado del formulario al salir de la pantalla
    fun resetFormState() {
        _formState.value = FormUiState.Idle
        _selectedTransaction.value = null
    }
}