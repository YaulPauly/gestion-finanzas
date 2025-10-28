package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.fintrack.mobile.ui.theme.data.Transaction
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token") val token: String,
    @Json(name = "userId") val userId: Int,
    @Json(name = "name") val name: String,
    @Json(name = "recentTransactions") val recentTransactions: List<Transaction>,
    @Json(name = "currentBalance") val currentBalance: BigDecimal,
    @Json(name = "monthlyIncome") val monthlyIncome: BigDecimal,
    @Json(name = "monthlyExpense") val monthlyExpense: BigDecimal
)