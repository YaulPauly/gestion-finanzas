package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.data.local.entity.MetaEntity
import pe.fintrack.mobile.data.repository.MetaRepository

class MetaViewModel (
    private val repository: MetaRepository
    ): ViewModel(){
    private val _metas = MutableStateFlow<List<MetaEntity>>(emptyList())
    val metas: StateFlow<List<MetaEntity>> = _metas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Opcional: Si el ID del usuario es estático o se pasa en el constructor, úsalo aquí.
    // Si el ID del usuario cambia, deberás manejarlo con una función pública de carga.
    private val currentUserId = 1 // Reemplaza con la lógica real para obtener el ID del usuario.

    init {
        collectAllMetas(currentUserId)
    }

    private fun collectAllMetas(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllMetas(usuarioId).collect { lista ->
                    _metas.value = lista
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar metas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun insertMeta(meta: MetaEntity) {
        viewModelScope.launch {
            try {
                repository.insertMeta(meta)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al insertar la meta: ${e.message}"
            }
        }
    }

    fun updateMeta(meta: MetaEntity) {
        viewModelScope.launch {
            try {
                repository.updateMeta(meta)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar la meta: ${e.message}"
            }
        }
    }

    fun deleteMeta(meta: MetaEntity) {
        viewModelScope.launch {
            try {
                repository.deletedMeta(meta)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar la meta: ${e.message}"
            }
        }
    }

    suspend fun getMetaById(id: Int): MetaEntity? {
        return try {
            repository.getMetaById(id)
        } catch (e: Exception) {
            _error.value = "Error al obtener la meta: ${e.message}"
            null
        }
    }

    fun asignarIngreso(metaId: Int, montoIngresado: Double) {
        viewModelScope.launch {
            try {
                repository.asignarIngreso(metaId, montoIngresado)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al asignar ingreso: ${e.message}"
            }
        }
    }

    fun marcarComoAlcanzada(metaId: Int) {
        viewModelScope.launch {
            try {
                repository.marcarComoAlcanzada(metaId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al marcar como alcanzada: ${e.message}"
            }
        }
    }
}