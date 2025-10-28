package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.model.AuthResponse
import pe.fintrack.mobile.data.model.LoginRequest

interface UsuarioRepository {
    // Definimos la función para el login. Es suspend porque la llamada a Retrofit lo es.
    suspend fun login(request: LoginRequest): AuthResponse

    // Aquí puedes añadir otras funciones relacionadas con el usuario, como 'getUserData()'
}