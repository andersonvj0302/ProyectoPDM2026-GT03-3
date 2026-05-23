package com.example.autotechs.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton que configura y provee la instancia de Retrofit.
 * Incluye un interceptor de logging para depuración de las peticiones HTTP.
 *
 * NOTA: Cambiar BASE_URL a la URL real del servidor backend cuando esté disponible.
 * Para emulador Android apuntando a localhost usar: "http://10.0.2.2:8080/"
 */
object RetrofitClient {

    // URL base de la API REST (cambiar cuando se tenga el backend real)
    private const val BASE_URL = "https://67ab5e985853dfff53dab929.mockapi.io/"

    /**
     * Interceptor de logging que muestra el cuerpo completo de las peticiones
     * y respuestas HTTP en Logcat. Útil para depuración durante el desarrollo.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp configurado con:
     * - Interceptor de logging para ver peticiones en Logcat
     * - Timeouts de 30 segundos para conexión, lectura y escritura
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia lazy del servicio API.
     * Se crea una sola vez y se reutiliza en toda la app.
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
