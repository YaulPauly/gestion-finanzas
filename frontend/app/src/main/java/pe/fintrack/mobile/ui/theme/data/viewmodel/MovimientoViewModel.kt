package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.io.IOException

sealed class MovimientoListaUiState {
    object Loading : MovimientoListaUiState()
    data class Success(val movements: List<Transaction>, val categories : List<Category>) : MovimientoListaUiState()
    data class Error(val message: String) : MovimientoListaUiState()
}

class MovimientoViewModel : ViewModel(){
    private val _uiState = MutableStateFlow<MovimientoListaUiState>(MovimientoListaUiState.Loading)
    val uiState: StateFlow<MovimientoListaUiState> = _uiState.asStateFlow()

    init {
        loadAllMovements()
    }

    fun loadAllMovements() {
        viewModelScope.launch {
            _uiState.value = MovimientoListaUiState.Loading

            try {
                // Lanza ambas llamadas en paralelo
                val incomesResponse = RetrofitClient.instance.getIncomes(page = 0, size = 20)
                val expensesResponse = RetrofitClient.instance.getExpenses(page = 0, size = 20)
                val categoriesResponse = RetrofitClient.instance.getCategories()

                if (incomesResponse.isSuccessful && expensesResponse.isSuccessful) {
                    val incomeList = incomesResponse.body()?.content ?: emptyList()
                    val expenseList = expensesResponse.body()?.content ?: emptyList()
                    val categoryList = categoriesResponse.body() ?: emptyList()
                    // Combina las listas y ordénalas
                    val combinedList = (incomeList + expenseList).sortedByDescending { it.date }
                    _uiState.value = MovimientoListaUiState.Success(combinedList,categoryList)
                } else {
                    _uiState.value = MovimientoListaUiState.Error("Error al cargar movimientos")
                }
            } catch (e: IOException) {
                _uiState.value = MovimientoListaUiState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = MovimientoListaUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }
}