package pe.fintrack.mobile.data.repository

import pe.fintrack.mobile.data.model.HomeUiState

interface HomeRepository {
    suspend fun getHomeData(): HomeUiState
}