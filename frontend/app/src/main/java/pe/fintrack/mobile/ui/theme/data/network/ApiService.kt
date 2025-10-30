package pe.fintrack.mobile.ui.theme.data.network

import pe.fintrack.mobile.ui.theme.data.model.Category
import pe.fintrack.mobile.ui.theme.data.Goal
import pe.fintrack.mobile.ui.theme.data.Transaction
import pe.fintrack.mobile.ui.theme.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- Autenticación ---
    @POST("api/auth/login") // Ruta relativa a la BASE_URL
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse> // @Body envía el objeto como JSON

    // --- Dashboard ---
    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(): Response<DashboardSummaryResponse> // GET no necesita @Body

    // --- Categorías ---
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>> // Devuelve una lista

    // --- Transacciones (Ingresos) ---
    @POST("api/transactions/income")
    suspend fun createIncome(@Body incomeRequest: IncomeRequest): Response<Transaction> // Devuelve la transacción creada

    @GET("api/transactions/income")
    suspend fun getIncomes(
        @Query("page") page: Int = 0, // Parámetro de consulta (?page=0)
        @Query("size") size: Int = 10 // Parámetro de consulta (&size=10)
    ): Response<PagedResult<Transaction>> // Respuesta paginada

    @PUT("api/transactions/income/{id}") // Asumiendo esta ruta para actualizar
    suspend fun updateIncome(
        @Path("id") transactionId: Long, // Reemplaza {id} en la URL
        @Body incomeRequest: IncomeRequest // Datos actualizados en el cuerpo
    ): Response<Transaction>

    @DELETE("api/transactions/income/{id}") // Asumiendo esta ruta para borrar
    suspend fun deleteIncome(@Path("id") transactionId: Long): Response<Unit> // No esperamos cuerpo en la respuesta OK (204 No Content)

    // --- Transacciones (Gastos) ---
    @POST("api/transactions/expense")
    suspend fun createExpense(@Body expenseRequest: ExpenseRequest): Response<Transaction>

    @GET("api/transactions/expense")
    suspend fun getExpenses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PagedResult<Transaction>>

    @PUT("api/transactions/expense/{id}") // Asumiendo ruta
    suspend fun updateExpense(
        @Path("id") transactionId: Long,
        @Body expenseRequest: ExpenseRequest
    ): Response<Transaction>

    @DELETE("api/transactions/expense/{id}") // Asumiendo ruta
    suspend fun deleteExpense(@Path("id") transactionId: Long): Response<Unit>

    // --- Metas de Ahorro ---
    @POST("api/goals")
    suspend fun createGoal(@Body goalRequest: GoalRequest): Response<Goal>

    @GET("api/goals")
    suspend fun getGoals(
        @Query("status") status: String? = null, // Filtro opcional por estado
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PagedResult<Goal>>

    @GET("api/goals/{id}")
    suspend fun getGoalById(@Path("id") goalId: Long): Response<Goal>

    @POST("api/goals/{id}/contributions")
    suspend fun contributeToGoal(
        @Path("id") goalId: Long,
        @Body contributionRequest: GoalContributionRequest
    ): Response<Goal> // Devuelve la meta actualizada

    @POST("api/goals/{id}/archive") // Asumiendo que es un POST
    suspend fun archiveGoal(@Path("id") goalId: Long): Response<Goal> // Devuelve la meta actualizada

    @GET("api/transaction/{id}")
    suspend fun getTransactionById(@Path("id") transactionId: Long): Response<Transaction>

}