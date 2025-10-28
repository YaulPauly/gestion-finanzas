package pe.fintrack.mobile.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.fintrack.mobile.data.model.AuthResponse
import pe.fintrack.mobile.data.model.DashboardSummary
import pe.fintrack.mobile.data.model.LoginRequest
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Singleton

interface FintrackApiService {

    /**
     * Endpoint para la autenticación (Login).
     * Ruta: /api/auth/login
     * Método: POST
     */
    @POST("api/auth/login")
    suspend fun loginUser(
        // @Body envía la data class LoginRequest como JSON al cuerpo de la petición
        @Body request: LoginRequest
    ): AuthResponse // Espera la data class AuthResponse como resultado


    /**
     * Endpoint protegido para obtener el resumen del dashboard.
     * Ruta: /api/dashboard/summary
     * Método: GET
     */
    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(
        // @Header se usa para enviar el token JWT en el formato "Bearer <TOKEN>"
        @Header("Authorization") token: String
    ): DashboardSummary
    // Nota: Usamos DashboardSummary, pero si la API devuelve AuthResponse, puedes usar esa.

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {

        // 1. Provee el objeto Moshi
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // 2. Provee el objeto Retrofit y lo configura
        @Provides
        @Singleton
        fun provideRetrofit(moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Tu URL base
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        // 3. Provee la interfaz del servicio (la API real)
        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): FintrackApiService {
            return retrofit.create(FintrackApiService::class.java)
        }
    }
}