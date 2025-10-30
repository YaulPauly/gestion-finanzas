package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.model.IncomeRequest
import pe.fintrack.mobile.ui.theme.data.network.ApiService
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseActionState
import pe.fintrack.mobile.ui.theme.data.viewmodel.ExpenseListUiState
import java.io.IOException
import java.math.BigDecimal


sealed class FormUiState {
    object Idle : FormUiState()
    object Loading : FormUiState()
    object Success : FormUiState()
    data class Error(val message: String) : FormUiState()
}

class TransactionViewModel(
    private val apiService: ApiService
) : ViewModel() {

    // --- Para el Dropdown de Categorías ---
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // --- Para la pantalla de Editar (carga los datos) ---
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    // --- ESTADOS DE INGRESOS (Income) ---
    private val _incomeListState = MutableStateFlow(ExpenseListUiState())
    val incomeListState: StateFlow<ExpenseListUiState> = _incomeListState.asStateFlow()
    private val _incomeActionState = MutableStateFlow<ExpenseActionState>(ExpenseActionState.Idle)
    val incomeActionState: StateFlow<ExpenseActionState> = _incomeActionState.asStateFlow()

    // --- Para el estado del formulario (Loading, Success, Error) ---
    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Idle)
    val formState: StateFlow<FormUiState> = _formState.asStateFlow()

    // Carga las categorías para el dropdown
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categories.value = response.body()!!
                }
            } catch (e: IOException) {
                // Manejar error de red
            }
        }
    }

    //  Crea un nuevo ingreso
    fun createIncome(montoStr: String, categoryId: Int?, description: String?, onSuccess: () -> Unit) {
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
                val response = apiService.createIncome(request)

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

    // Carga datos de un ingreso existente (para Editar)
    fun loadTransactionDetails(id: Long) {
        viewModelScope.launch {
            _formState.value = FormUiState.Loading
            try {
                // ✅ Usa apiService inyectado
                val response = apiService.getTransactionDetails(id)

                if (response.isSuccessful && response.body() != null) {
                    _selectedTransaction.value = response.body()
                    _formState.value = FormUiState.Idle
                } else {
                    _formState.value = FormUiState.Error("No se pudo cargar la transacción")
                }
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

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
                val response = apiService.updateIncome(id, request)

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

    fun resetFormState() {
        _formState.value = FormUiState.Idle
        _selectedTransaction.value = null
    }
}