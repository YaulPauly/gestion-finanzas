package pe.fintrack.mobile.data.model

data class HomeUiState(
    val nombreUsuario: String = "",
    val saldoActual: Double = 0.0,
    val resumen: Resumen = Resumen(0.0, 0.0),
    val movimientosRecientes: List<Movimiento> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
