package pe.fintrack.mobile.ui.theme.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PagedResult<T>(
    @Json(name = "content") val content: List<T>,          // La lista de elementos en la página actual
    @Json(name = "totalPages") val totalPages: Int,        // Número total de páginas
    @Json(name = "totalElements") val totalElements: Long, // Número total de elementos en todas las páginas
    @Json(name = "number") val number: Int,                // Número de la página actual (base 0)
    @Json(name = "size") val size: Int,                    // Tamaño de la página
    @Json(name = "first") val first: Boolean,              // ¿Es la primera página?
    @Json(name = "last") val last: Boolean,                // ¿Es la última página?
    @Json(name = "empty") val empty: Boolean               // ¿Está vacía la página actual?
)