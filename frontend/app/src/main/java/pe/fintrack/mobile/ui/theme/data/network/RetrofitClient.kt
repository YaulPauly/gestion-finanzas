package pe.fintrack.mobile.ui.theme.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://gestion-finanzas.onrender.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout de lectura
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout de escritura
            .addInterceptor(AuthInterceptor())
            .build()
    }


    // Configuración de Moshi
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(BigDecimalAdaptador)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // Instancia de Retrofit (configurada con todo lo anterior)
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Usamos Moshi para JSON
            .build()
            .create(ApiService::class.java) // Crea la implementación de nuestra interfaz
    }
}

