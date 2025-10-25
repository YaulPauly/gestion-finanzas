package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.model.HomeUiState
import pe.fintrack.mobile.data.repository.HomeRepository
import pe.fintrack.mobile.data.repository.UsuarioRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        fetchHomeData()
    }
    private fun loadUserData() {
        viewModelScope.launch {
            // 2. OBTENER EL NOMBRE DEL REPOSITORIO
            val usuario = usuarioRepository.getLoggedInUser()

            // 3. ACTUALIZAR EL ESTADO
            if (usuario != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        nombreUsuario = usuario.nombre,
                    )
                }
            } else {
                // redirecionar al loguin
            }
        }
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Obtener la data (saldos, movimientos, etc.) desde el repositorio
                val newDataFromRepo = homeRepository.getHomeData()

                _uiState.update { currentState ->
                    currentState.copy(

                        saldoActual = newDataFromRepo.saldoActual,
                        resumen = newDataFromRepo.resumen,
                        movimientosRecientes = newDataFromRepo.movimientosRecientes,

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
    }