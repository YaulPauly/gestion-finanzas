package pe.fintrack.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pe.fintrack.mobile.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert
    suspend fun registrar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun iniciarSesion(email: String, password: String): UsuarioEntity?
}