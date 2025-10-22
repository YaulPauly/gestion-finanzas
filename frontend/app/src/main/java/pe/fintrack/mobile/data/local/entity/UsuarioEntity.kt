package pe.fintrack.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(@PrimaryKey(autoGenerate = true)
                         val id: Long = 0,
                         val nombre: String,
                         val email: String? = null,
                         val password: String? = null){
}
/**
 * Relaciones entre Entidades:
 * Un Usuario puede tener muchas Cuentas.
 * Una Cuenta puede tener muchas Transacciones.
 * Una Categoria puede estar asociada a muchas Transacciones.
 * Una Transaccion tiene una Cuenta y una Categoria.
 */

