package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "userId") val userId: Int,
    @Json(name = "createdAt") val createdAt: String
)