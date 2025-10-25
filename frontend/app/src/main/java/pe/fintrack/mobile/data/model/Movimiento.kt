package pe.fintrack.mobile.data.model

data class Movimiento(
    val id: Long,
    val categoria: String,
    val fecha: String,
    val monto: Double,
    val esIngreso: Boolean
)
