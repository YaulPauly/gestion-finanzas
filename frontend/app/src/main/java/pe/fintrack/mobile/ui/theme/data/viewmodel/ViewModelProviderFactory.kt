package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.fintrack.mobile.ui.theme.data.network.ApiService
import pe.fintrack.mobile.ui.viewmodel.TransactionViewModel

class ExpenseViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //  Manejar ExpenseViewModel (Gastos)
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(apiService) as T
        }
        //  Manejar TransactionViewModel (Ingresos)
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(apiService) as T
        }
        // Si no es ninguno de los dos, lanza la excepción
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}