package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.local.entity.UsuarioEntity
import pe.fintrack.mobile.data.repository.UsuarioRepository

class UsuarioViewModel (private val repository: UsuarioRepository
) : ViewModel() {
    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()


    fun registrar(usuario: UsuarioEntity) {
        _message.value = null

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newId = repository.registrar(usuario)

                if (newId > 0) {
                    _message.value = "Registro exitoso."
                } else {
                    _message.value = "Error: El usuario no pudo ser registrado."
                }
            } catch (e: Exception) {
                _message.value = "Error al registrar el usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun iniciarSesion(email: String, password: String) {
        _message.value = null

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usuario = repository.iniciarSesion(email, password)

                _usuarioActual.value = usuario

                if (usuario != null) {
                    _message.value = "¡Bienvenido/a, ${usuario.nombre}!"
                } else {
                    _message.value = "Credenciales incorrectas. Intenta de nuevo."
                }
            } catch (e: Exception) {
                _message.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        _message.value = "Sesión cerrada."
    }

    /**
     * Función para limpiar el mensaje de error/éxito después de que la UI lo muestre.
     */
    fun clearMessage() {
        _message.value = null
    }
}