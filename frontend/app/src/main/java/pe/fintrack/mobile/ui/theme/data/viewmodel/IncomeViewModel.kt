package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.io.IOException

class IncomeViewModel : ViewModel(){
    // StateFlow privado y mutable para uso interno
    private val _uiState = MutableStateFlow<IncomeListUiState>(IncomeListUiState.Loading)
    // StateFlow público para que la UI observe los cambios
    val uiState: StateFlow<IncomeListUiState> = _uiState.asStateFlow()

    init {
        loadIncomes() // Llama a la API en cuanto se crea el ViewModel
    }

    // Función para cargar los ingresos desde la API
    fun loadIncomes() {
        viewModelScope.launch {
            _uiState.value = IncomeListUiState.Loading // Pone el estado en "Cargando"
            try {
                // Llama a la API (pide página 0, tamaño 20 por defecto)
                val response = RetrofitClient.instance.getIncomes(page = 0, size = 20)

                if (response.isSuccessful && response.body() != null) {
                    // Éxito: Actualiza el estado con la lista de ingresos
                    _uiState.value = IncomeListUiState.Success(response.body()!!.content)
                } else {
                    // Error del servidor (ej: 404, 500)
                    _uiState.value = IncomeListUiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {
                // Error de red (sin internet, timeout)
                _uiState.value = IncomeListUiState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                // Otro error inesperado
                _uiState.value = IncomeListUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }
}