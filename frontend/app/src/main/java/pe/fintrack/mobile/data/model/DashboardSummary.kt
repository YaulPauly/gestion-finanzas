package pe.fintrack.mobile.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DashboardSummary(
    val currentBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val recentTransactions: List<Transaccion>
)