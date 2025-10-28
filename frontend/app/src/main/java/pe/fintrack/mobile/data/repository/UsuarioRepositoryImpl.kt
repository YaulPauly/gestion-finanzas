package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.model.AuthResponse
import pe.fintrack.mobile.data.model.LoginRequest
import pe.fintrack.mobile.data.remote.FintrackApiService
import pe.fintrack.mobile.data.remote.RetrofitClient // Importamos el cliente de red
import javax.inject.Inject // Esto se usa si estás usando Hilt/Koin

class UsuarioRepositoryImpl @Inject constructor(
    // Inyectar el servicio de Retrofit a través de Hilt
    private val apiService: FintrackApiService
) : UsuarioRepository {

    override suspend fun login(request: LoginRequest): AuthResponse {
        return try {
            // Usar la interfaz inyectada, no el objeto global RetrofitClient
            return apiService.loginUser(request)
        } catch (e: Exception) {
            throw e
        }
    }
}