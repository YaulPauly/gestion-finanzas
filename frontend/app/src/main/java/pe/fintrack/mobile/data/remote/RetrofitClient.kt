package pe.fintrack.mobile.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    // **IMPORTANTE**: La URL base de tu API Fintrack
    private const val BASE_URL = "https://gestion-finanzas.onrender.com/"

    private val moshi = Moshi.Builder()
        // Añade el adaptador para Kotlin (maneja data classes)
        .add(KotlinJsonAdapterFactory())
        .build()

    // Inicialización perezosa (lazy) de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Objeto de servicio que se usará en los Repositories y ViewModels
    val apiService: FintrackApiService by lazy {
        retrofit.create(FintrackApiService::class.java)
    }
}