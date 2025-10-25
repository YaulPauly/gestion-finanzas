package pe.fintrack.mobile.data.repository

import kotlinx.coroutines.delay
import pe.fintrack.mobile.data.model.HomeUiState
import pe.fintrack.mobile.data.model.Movimiento
import pe.fintrack.mobile.data.model.Resumen

class HomeRepositoryImpl : HomeRepository {

    // Función que simula la obtención de datos (en duro por ahora)
    override suspend fun getHomeData(): HomeUiState {
        // Simular un retraso de red de 2 segundos
        delay(2000)

        val saldo = 4500.50
        val ingresos = 4500.00
        val gastos = 450.00

        val movimientos = listOf(
            Movimiento(1, "Trabajo", "24 Ago 2025 13:54 PM", 1500.00, true),
            Movimiento(2, "Supermercado", "23 Ago 2025 10:30 AM", 120.50, false),
            Movimiento(3, "Freelance", "22 Ago 2025 08:00 PM", 800.00, true),
        )

        return HomeUiState(
            saldoActual = saldo,
            resumen = Resumen(ingresos, gastos),
            movimientosRecientes = movimientos,
            isLoading = false
        )
    }
}