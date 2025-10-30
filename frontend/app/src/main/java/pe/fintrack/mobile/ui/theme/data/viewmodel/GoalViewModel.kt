package pe.fintrack.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fintrack.mobile.ui.theme.data.Goal
import pe.fintrack.mobile.ui.theme.data.GoalStatus
import pe.fintrack.mobile.ui.theme.data.model.GoalContributionRequest
import pe.fintrack.mobile.ui.theme.data.model.GoalRequest
import pe.fintrack.mobile.ui.theme.data.network.RetrofitClient
import java.math.BigDecimal

// Estado para la lista de metas
sealed class GoalListUiState {
    object Loading : GoalListUiState()
    data class Success(val goals: List<Goal>) : GoalListUiState()
    data class Error(val message: String) : GoalListUiState()
}

// Estado para los formularios (Crear y Contribuir)
sealed class GoalFormUiState {
    object Idle : GoalFormUiState()
    object Loading : GoalFormUiState()
    object Success : GoalFormUiState()
    data class Error(val message: String) : GoalFormUiState()
}

class GoalViewModel : ViewModel() {

    // --- Para ListaMetasScreen ---
    private val _listState = MutableStateFlow<GoalListUiState>(GoalListUiState.Loading)
    val listState: StateFlow<GoalListUiState> = _listState.asStateFlow()

    // --- Para CrearMetaScreen y ContribuirMetaScreen ---
    private val _formState = MutableStateFlow<GoalFormUiState>(GoalFormUiState.Idle)
    val formState: StateFlow<GoalFormUiState> = _formState.asStateFlow()

    // --- Para ContribuirMetaScreen (guarda la meta seleccionada) ---
    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    // --- Para disparar el Modal de "Meta Alcanzada" ---
    private val _goalAchievedEvent = MutableStateFlow<Goal?>(null)
    val goalAchievedEvent: StateFlow<Goal?> = _goalAchievedEvent.asStateFlow()

    init {
        loadGoals()
    }

    /**
     * Carga la lista de metas para la pantalla principal de Metas.
     */
    fun loadGoals() {
        viewModelScope.launch {
            _listState.value = GoalListUiState.Loading
            try {
                val response = RetrofitClient.instance.getGoals(page = 0, size = 10)
                if (response.isSuccessful && response.body() != null) {
                    _listState.value = GoalListUiState.Success(response.body()!!.content)
                } else {
                    _listState.value = GoalListUiState.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                _listState.value = GoalListUiState.Error(e.message ?: "Error de red")
            }
        }
    }

    /**
     * Lógica para CrearMetaScreen
     */
    fun createGoal(nombre: String, objetivoStr: String, descripcion: String?, onSuccess: () -> Unit) {
        val target = objetivoStr.toBigDecimalOrNull()
        if (nombre.isBlank() || target == null || target <= BigDecimal.ZERO) {
            _formState.value = GoalFormUiState.Error("Nombre y monto objetivo son obligatorios.")
            return
        }

        viewModelScope.launch {
            _formState.value = GoalFormUiState.Loading
            try {
                val request = GoalRequest(nombre, target, descripcion.takeIf { !it.isNullOrBlank() })
                val response = RetrofitClient.instance.createGoal(request)

                if (response.isSuccessful) {
                    _formState.value = GoalFormUiState.Success
                    loadGoals() // Refresca la lista de metas
                    onSuccess() // Navega hacia atrás
                } else {
                    _formState.value = GoalFormUiState.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                _formState.value = GoalFormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    /**
     * Lógica para ContribuirMetaScreen: Carga los detalles de la meta.
     */
    fun loadGoalDetails(goalId: Long) {
        viewModelScope.launch {
            _formState.value = GoalFormUiState.Loading
            _selectedGoal.value = null // Limpia el valor anterior
            try {
                val response = RetrofitClient.instance.getGoalById(goalId)
                if (response.isSuccessful) {
                    _selectedGoal.value = response.body()
                    _formState.value = GoalFormUiState.Idle
                } else {
                    _formState.value = GoalFormUiState.Error("No se pudo cargar la meta")
                }
            } catch (e: Exception) {
                _formState.value = GoalFormUiState.Error(e.message ?: "Error de red")
            }
        }
    }

    /**
     * Lógica para ContribuirMetaScreen: Transfiere el dinero.
     */
    fun addContribution(amountStr: String, onSuccess: () -> Unit) {
        val goal = _selectedGoal.value
        if (goal == null) {
            _formState.value = GoalFormUiState.Error("No hay una meta seleccionada")
            return
        }

        val amount = amountStr.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _formState.value = GoalFormUiState.Error("Monto inválido")
            return
        }

        val savedAmount = goal.savedAmount ?: BigDecimal.ZERO
        val remaining = goal.target - savedAmount

        if (amount > remaining) {
            _formState.value = GoalFormUiState.Error("El monto no puede ser mayor al restante (S/ ${remaining.toPlainString()})")
            return
        }

        viewModelScope.launch {
            _formState.value = GoalFormUiState.Loading
            try {
                val request = GoalContributionRequest(amount)
                val response = RetrofitClient.instance.contributeToGoal(goal.id, request)

                if (response.isSuccessful && response.body() != null) {
                    val updatedGoal = response.body()!!
                    _formState.value = GoalFormUiState.Success

                    // --- LÓGICA DEL MODAL: Si la meta se acaba de alcanzar ---
                    if (updatedGoal.status == GoalStatus.ACHIEVED && goal.status != GoalStatus.ACHIEVED) {
                        _goalAchievedEvent.value = updatedGoal // Dispara el modal
                    } else {
                        onSuccess() // Si no se completó, solo navega atrás
                    }
                    loadGoals() // Refresca la lista de metas en segundo plano
                } else {
                    // Muestra el error del backend (ej: "Insufficient funds")
                    val errorMsg = response.errorBody()?.string() ?: "Error al transferir"
                    _formState.value = GoalFormUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _formState.value = GoalFormUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    /**
     * Limpia el evento del modal para que no se muestre de nuevo.
     */
    fun clearGoalAchievedEvent() {
        _goalAchievedEvent.value = null
    }

    /**
     * Limpia los estados del formulario al salir de la pantalla.
     */
    fun resetFormState() {
        _formState.value = GoalFormUiState.Idle
        _selectedGoal.value = null
    }
}