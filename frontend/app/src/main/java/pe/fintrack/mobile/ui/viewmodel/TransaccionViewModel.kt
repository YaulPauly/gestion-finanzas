package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.local.entity.TransaccionEntity
import pe.fintrack.mobile.data.repository.TransaccionRepository

class TransaccionViewModel (
    private val repository: TransaccionRepository
) : ViewModel(){

    private val _transacciones = MutableStateFlow<List<TransaccionEntity>>(emptyList())
    val transacciones: StateFlow<List<TransaccionEntity>> = _transacciones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        collectAllTransacciones()
    }
    private fun collectAllTransacciones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTransactions().collect { lista ->
                    _transacciones.value = lista
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar transacciones: ${e.message}"
            } finally {
                // Oculta el estado de carga (solo se ejecutará la primera vez con el Flow)
                _isLoading.value = false
            }
        }
    }
    fun insertTransaccion(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            try {
                repository.insertTransaccion(transaccion)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al insertar la transacción: ${e.message}"
            }
        }
    }
    fun updateTransaccion(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            try {
                repository.updateTransaccion(transaccion)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar la transacción: ${e.message}"
            }
        }
    }
    fun deleteTransaccion(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            try {
                repository.deletedTransaccion(transaccion)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar la transacción: ${e.message}"
            }
        }
    }
    suspend fun getTransaccionById(id: Int): TransaccionEntity? {
        return try {
            repository.getTransactionsById(id)
        } catch (e: Exception) {
            _error.value = "Error al obtener la transacción: ${e.message}"
            null
        }
    }
}