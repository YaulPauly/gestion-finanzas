package pe.fintrack.mobile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transaccion(

    val id: Long,
    val date: String,
    val amount: Double, // Moshi puede manejar BigDecimal, pero Double es más común en móviles.
    val description: String?,
    val type: String,
    val categoryId: Int,
    val goalId: Int?,
    val userId: Int,
    val createdAt: String // Usar String para fecha y hora (luego se puede parsear)
)