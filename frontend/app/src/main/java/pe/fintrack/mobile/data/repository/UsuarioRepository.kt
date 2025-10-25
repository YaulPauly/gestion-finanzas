package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.local.entity.UsuarioEntity
import javax.inject.Inject

class UsuarioRepository @Inject constructor(private val UsuarioDao: UsuarioDao) {
    // Usuario de prueba para simular que está "registrado"
    private val mockUser = UsuarioEntity(
        id = 1,
        email = "usuario@ejemplo.com",
        password = "123",
        nombre = "Alex FinTrack"
    )

    suspend fun registrar(usuario: UsuarioEntity): Long {
        // Simula un delay de red y un registro exitoso
        kotlinx.coroutines.delay(1000)
        return 1L // ID asignado
    }

    suspend fun iniciarSesion(email: String, password: String): UsuarioEntity? {
        // Simula un delay de red
        kotlinx.coroutines.delay(2000)

        // Verifica con el usuario de prueba
        return if (email == mockUser.email && password == mockUser.password) {
            mockUser
        } else {
            null
        }
    }
}