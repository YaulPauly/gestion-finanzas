package pe.fintrack.mobile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    // El @Json(name = "token") es opcional si el nombre del campo Kotlin es igual
    @field:Json(name = "token")
    val token: String,
    val userId: Int,
    val name: String,

    val currentBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,

    val recentTransactions: List<Transaccion>
)
