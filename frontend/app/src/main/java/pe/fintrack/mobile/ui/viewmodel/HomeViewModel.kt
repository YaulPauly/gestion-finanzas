package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.model.DashboardSummary
import pe.fintrack.mobile.data.model.HomeUiState
import pe.fintrack.mobile.data.model.Movimiento
import pe.fintrack.mobile.data.model.Transaccion
import pe.fintrack.mobile.data.repository.HomeRepository
import pe.fintrack.mobile.di.SessionManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Recolectar el token del SessionManager
            sessionManager.token.collect { token ->
                // Si el token es válido, cargar la data del dashboard
                if (token != null) {
                    fetchHomeData(token)
                }
            }
        }
    }


    fun fetchHomeData(authToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val nombreUsuario = sessionManager.userName.value ?: "Usuario Fintrack"

            try {
                // CORRECCIÓN 1: El token se usa desde el argumento authToken
                val summary: DashboardSummary = homeRepository.getDashboardSummary(authToken)

                _uiState.update { currentState ->
                    currentState.copy(
                        nombreUsuario = nombreUsuario,
                        saldoActual = summary.currentBalance,
                        resumen = currentState.resumen.copy(
                            ingresos = summary.monthlyIncome,
                            gastos = summary.monthlyExpense
                        ),
                        // CORRECCIÓN 2: Se llama a la función de mapeo definida abajo
                        movimientosRecientes = mapApiTransactionsToLocal(summary.recentTransactions),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }

    private fun mapApiTransactionsToLocal(apiList: List<Transaccion>?): List<Movimiento> {
        // Manejamos el caso de que la lista de la API sea null
        if (apiList.isNullOrEmpty()) return emptyList()
        return apiList.map { apiTrans ->
            val idValue: Long = apiTrans.id ?: 0L

            // Mapeo de Transaccion (API) a Movimiento (Local/UI)
            Movimiento(
                id = idValue,
                categoria = apiTrans.description ?: "Sin categoría",
                fecha = apiTrans.date,
                monto = apiTrans.amount,
                esIngreso = apiTrans.type == "INGRESO"
            )
        }
    }
}