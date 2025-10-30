package pe.fintrack.mobile.ui.theme.data.viewmodel

import pe.fintrack.mobile.ui.theme.data.Transaction

sealed class IncomeListUiState{
    object Loading: IncomeListUiState()
    data class Success(val incomes: List<Transaction>) : IncomeListUiState()
    data class Error(val message: String) : IncomeListUiState()
}