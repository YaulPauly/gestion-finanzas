package pe.fintrack.mobile.ui.theme.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class Goal(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "target") val target: BigDecimal,
    @Json(name = "savedAmount") val savedAmount: BigDecimal?,
    @Json(name = "description") val description: String?,
    @Json(name = "status") val status: GoalStatus,
    @Json(name = "userId") val userId: Int,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?
)

// Enum para los estados
enum class GoalStatus {
    IN_PROGRESS,
    ACHIEVED,
    ARCHIVED
}