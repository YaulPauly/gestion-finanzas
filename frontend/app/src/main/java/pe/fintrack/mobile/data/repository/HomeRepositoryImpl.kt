package pe.fintrack.mobile.data.repository

import kotlinx.coroutines.delay
import pe.fintrack.mobile.data.model.DashboardSummary
import pe.fintrack.mobile.data.model.HomeUiState
import pe.fintrack.mobile.data.model.Movimiento
import pe.fintrack.mobile.data.model.Resumen
import pe.fintrack.mobile.data.remote.FintrackApiService
import pe.fintrack.mobile.data.remote.RetrofitClient
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: FintrackApiService // Inyecta la interfaz de Retrofit
) : HomeRepository {

    override suspend fun getDashboardSummary(authToken: String): DashboardSummary {
        // El token DEBE tener el prefijo "Bearer "
        val tokenWithBearer = "Bearer $authToken"

        return apiService.getDashboardSummary(tokenWithBearer)
    }

}