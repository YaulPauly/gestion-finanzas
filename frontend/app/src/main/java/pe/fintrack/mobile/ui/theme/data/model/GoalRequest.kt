package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class GoalRequest(
    @Json(name = "name") val name: String,
    @Json(name = "target") val target: BigDecimal,
    @Json(name = "description") val description: String?
)
