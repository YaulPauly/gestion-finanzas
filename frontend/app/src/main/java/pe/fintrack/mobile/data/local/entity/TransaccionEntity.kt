package pe.fintrack.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transacciones",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT // No permitir borrar Categoria si tiene Transacciones asociadas
        ),
        ForeignKey(
            entity = MetaEntity::class,
            parentColumns = ["id"],
            childColumns = ["metaId"],
            onDelete = ForeignKey.SET_NULL // Si se borra la Meta, el campo se pone a NULL
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: Long,
    val monto: Double,
    val descripcion: String,
    val tipoTransaccion: TipoTransaccion,
    val categoriaId: Int,
    val metaId: Int?,
    val usuarioId: Int
)