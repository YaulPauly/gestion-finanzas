package pe.fintrack.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metas",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetaEntity(@PrimaryKey(autoGenerate = true)
                      val id: Int = 0,
                      val nombre: String,
                      val objetivo: Double,
                      val acumulado: Double = 0.0,
                      val fechaLimite: Long, // Usará el TypeConverter
                      val estado: EstadoMeta = EstadoMeta.EN_PROGRESO,
                      val usuarioId: Int
)
