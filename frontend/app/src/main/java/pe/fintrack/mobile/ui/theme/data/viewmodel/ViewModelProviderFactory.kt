package pe.fintrack.mobile.ui.theme.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.fintrack.mobile.ui.theme.data.network.ApiService

class ExpenseViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    // Sobreescribe el método create para instanciar el ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}