package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ExpenseRequest(
    @Json(name = "amount") val amount: BigDecimal,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "description") val description: String?
)