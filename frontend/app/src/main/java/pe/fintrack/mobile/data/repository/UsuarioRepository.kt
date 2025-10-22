package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.local.entity.UsuarioEntity

class UsuarioRepository(private val UsuarioDao: UsuarioDao) {
    suspend fun registrar(usuario: UsuarioEntity): Long{
        return UsuarioDao.registrar(usuario)
    }

    suspend fun iniciarSesion(email: String, password: String): UsuarioEntity?{
        return UsuarioDao.iniciarSesion(email, password)
    }
}