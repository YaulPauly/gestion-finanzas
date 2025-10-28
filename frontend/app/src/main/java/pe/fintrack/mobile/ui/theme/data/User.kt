package pe.fintrack.mobile.ui.theme.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "currentBalance") val currentBalance: BigDecimal?, // Puede ser nulo
    @Json(name = "createdAt") val createdAt: String
)