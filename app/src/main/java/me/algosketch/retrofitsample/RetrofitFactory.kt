package me.algosketch.retrofitsample

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    inline fun <reified Service> create(): Service {
        return Retrofit.Builder()
            .baseUrl("https://me.algosketch/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(createLoggingInterceptor())
                    .addInterceptor(createMockingData())
                    .build()
            )
            .build()
            .create(Service::class.java)
    }

    fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun createMockingData(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()

            okhttp3.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("")
                .code(200)
                .body("{\"title\": \"test\", \"body\": \"Hello Retrofit!\"}".toResponseBody())
                .build()
        }
    }
}