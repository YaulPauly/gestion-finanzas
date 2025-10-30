package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.fintrack.mobile.ui.theme.data.Transaction
import java.math.BigDecimal

// Nota: Renombrado para coincidir con el DTO del backend
@JsonClass(generateAdapter = true)
data class DashboardSummaryResponse(
    @Json(name = "currentBalance") val currentBalance: BigDecimal,
    @Json(name = "monthlyIncome") val monthlyIncome: BigDecimal,
    @Json(name = "monthlyExpense") val monthlyExpense: BigDecimal,
    @Json(name = "recentTransactions") val recentTransactions: List<Transaction>
)