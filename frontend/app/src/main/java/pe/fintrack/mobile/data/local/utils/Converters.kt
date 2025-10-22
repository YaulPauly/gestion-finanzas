package pe.fintrack.mobile.data.local.utils

import androidx.room.TypeConverter
import pe.fintrack.mobile.data.local.entity.EstadoMeta
import pe.fintrack.mobile.data.local.entity.TipoTransaccion

class Converters {
    // Conversor para TipoTransaccion (Enum -> String)
    @TypeConverter
    fun fromTipoTransaccion(value: TipoTransaccion?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTipoTransaccion(value: String?): TipoTransaccion? {
        return value?.let { TipoTransaccion.valueOf(it) }
    }

    // Conversor para EstadoMeta (Enum -> String)
    @TypeConverter
    fun fromEstadoMeta(value: EstadoMeta?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEstadoMeta(value: String?): EstadoMeta? {
        return value?.let { EstadoMeta.valueOf(it) }
    }
}