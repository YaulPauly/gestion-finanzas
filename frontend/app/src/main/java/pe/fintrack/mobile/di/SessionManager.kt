package pe.fintrack.mobile.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.fintrack.mobile.data.model.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    // Usamos StateFlow para que el HomeViewModel pueda reaccionar a los cambios
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _userName = MutableStateFlow<String?>("Usuario Fintrack")
    val userName: StateFlow<String?> = _userName.asStateFlow()

    fun saveSession(response: AuthResponse) {
        _token.value = response.token
        _userName.value = response.name
    }

    fun clearSession() {
        _token.value = null
        _userName.value = null
    }
}