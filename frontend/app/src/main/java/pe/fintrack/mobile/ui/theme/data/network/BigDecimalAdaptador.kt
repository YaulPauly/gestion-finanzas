package pe.fintrack.mobile.ui.theme.data.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

object BigDecimalAdaptador {
    @FromJson
    fun fromJson(string: String): BigDecimal ? {
        return try {
            BigDecimal(string)
        } catch (e: NumberFormatException) {
            null
        }
    }

    @ToJson
    fun toJson(value: BigDecimal): String {
        return value.toPlainString() // toPlainString evita notación científica
    }

}