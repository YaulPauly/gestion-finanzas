package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.model.AuthResponse
import pe.fintrack.mobile.data.model.LoginRequest
import pe.fintrack.mobile.data.repository.UsuarioRepository
import pe.fintrack.mobile.di.SessionManager
import javax.inject.Inject

// 1. Define el estado de la UI
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val response: AuthResponse) : AuthUiState() // Devuelve la respuesta de la API
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                // 2. Crea el objeto de petición y llama al Repositorio
                val request = LoginRequest(email, password)
                val response = authRepository.login(request)
                sessionManager.saveSession(response)

                // 3. Éxito y manejo de la respuesta
                _uiState.value = AuthUiState.Success(response)

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Error desconocido"
                _uiState.value = AuthUiState.Error("Fallo en la autenticación: $errorMessage")
            }
        }
    }
}