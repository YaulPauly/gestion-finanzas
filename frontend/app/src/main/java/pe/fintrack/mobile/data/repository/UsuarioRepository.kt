package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.local.entity.UsuarioEntity
import javax.inject.Inject

class UsuarioRepository @Inject constructor(private val UsuarioDao: UsuarioDao) {
    // 1. SIMULAR ALMACENAMIENTO DE SESIÓN ACTIVA
    companion object {
        var activeUser: UsuarioEntity? = null // Variable estática para el usuario logueado
    }
    private val mockUser = UsuarioEntity(
        id = 1,
        email = "usuario@ejemplo.com",
        password = "123",
        nombre = "Alex FinTrack"
    )

    suspend fun registrar(usuario: UsuarioEntity): Long {
        // Simula un delay de red y un registro exitoso
        kotlinx.coroutines.delay(1000)
        return 1L
    }

    suspend fun iniciarSesion(email: String, password: String): UsuarioEntity? {
        kotlinx.coroutines.delay(2000)
        // Verifica con el usuario de prueba
        return if (email == mockUser.email && password == mockUser.password) {
            // 2. GUARDAR EL USUARIO ACTIVO AL INICIAR SESIÓN
            activeUser = mockUser
            mockUser
        } else {
            activeUser = null
            null
        }
    }
    suspend fun getLoggedInUser(): UsuarioEntity? {
        // Aquí simplemente devolvemos el usuario que guardamos en la variable estática.
        return activeUser
    }
}