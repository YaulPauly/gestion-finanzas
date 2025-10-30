package pe.fintrack.mobile.ui.theme.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "id") val id: Long,
    @Json(name = "date") val date: String,
    @Json(name = "amount") val amount: BigDecimal,
    @Json(name = "description") val description: String?,
    @Json(name = "type") val type: TransactionType,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "goalId") val goalId: Long?,
    @Json(name = "userId") val userId: Int,
    @Json(name = "createdAt") val createdAt: String
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
