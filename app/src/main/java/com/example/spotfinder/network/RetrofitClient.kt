package com.example.spotfinder.network

import android.util.Log
import com.example.spotfinder.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://3.220.100.170/api/v1/"

    private var sessionManager: SessionManager? = null

    fun init(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    init {
        Log.i("RetrofitClient", "BASE_URL set to $BASE_URL")
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        // Añadir interceptor de auth si SessionManager fue inicializado
        sessionManager?.let {
            builder.addInterceptor(AuthInterceptor(it))
        }

        return builder.build()
    }

    val instance: ApiService by lazy {
        Log.i("RetrofitClient", "Creating Retrofit instance with baseUrl=$BASE_URL")
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
