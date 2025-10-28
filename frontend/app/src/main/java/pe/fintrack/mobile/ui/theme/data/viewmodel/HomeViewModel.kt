package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.model.DashboardSummaryResponse
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.io.IOException

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val summary: DashboardSummaryResponse) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class HomeViewModel : ViewModel() {
    private val _summaryState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val summaryState: StateFlow<DashboardUiState> = _summaryState.asStateFlow()

    init {
        loadDashboardSummary()
    }

    fun loadDashboardSummary() {
        viewModelScope.launch {
            _summaryState.value = DashboardUiState.Loading
            try{
                val response = RetrofitClient.instance.getDashboardSummary()
                if(response.isSuccessful && response.body() != null){
                    _summaryState.value = DashboardUiState.Success(response.body()!!)

                } else {
                    if(response.code() == 401 || response.code() == 403){
                        _summaryState.value = DashboardUiState.Error("Sesión inválida. Por favor inicia sesión de nuevo")
                    } else {
                        _summaryState.value = DashboardUiState.Error("Error al cargar resumen: ${response.code()}")
                    }
                }
            } catch (e: IOException) {
                _summaryState.value = DashboardUiState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                _summaryState.value = DashboardUiState.Error("Error inesperado: ${e.message}")
            }
                }
            }



}