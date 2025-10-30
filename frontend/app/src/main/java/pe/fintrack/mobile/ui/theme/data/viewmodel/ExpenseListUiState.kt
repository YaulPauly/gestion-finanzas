package pe.fintrack.mobile.ui.theme.data.viewmodel

import pe.fintrack.mobile.ui.theme.data.Transaction

data class ExpenseListUiState(
    // Indica si se está cargando la lista (muestra CircularProgressIndicator)
    val isLoading: Boolean = false,

    // Contiene la lista real de gastos obtenida de la API
    // Si usas paginación, esta lista se irá acumulando o re-poblando.
    val expenses: List<Transaction> = emptyList(),

    // Almacena un mensaje de error si la llamada a la API falla
    val error: String? = null,

    // Opcional: Para manejar la paginación de manera más completa
    val isPaginating: Boolean = false,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
)