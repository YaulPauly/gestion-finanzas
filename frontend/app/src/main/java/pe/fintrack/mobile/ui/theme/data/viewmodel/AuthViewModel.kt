package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.TokenManager
import pe.fintrack.mobile.ui.theme.data.model.LoginRequest
import pe.fintrack.mobile.ui.theme.data.model.LoginResponse
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.io.IOException


sealed class LoginUiState {
    object Idle: LoginUiState()
    object Loading: LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        // Inicia una coroutine en el scope del ViewModel
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading // Cambia el estado a cargando
            try {
                // Llama a la API usando el cliente Retrofit
                val response = RetrofitClient.instance.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {

                    val loginData = response.body()!!
                    TokenManager.saveSession(loginData)
                    _loginState.value = LoginUiState.Success(loginData)
                } else {
                    // Error HTTP (ej: 401 Unauthorized)
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _loginState.value = LoginUiState.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: IOException) {
                // Error de red (sin conexión, timeout, etc.)
                _loginState.value = LoginUiState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                // Otros errores (ej: JSON malformado, error inesperado)
                _loginState.value = LoginUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun logout() {
        TokenManager.clearSession()
        _loginState.value = LoginUiState.Idle
    }

}