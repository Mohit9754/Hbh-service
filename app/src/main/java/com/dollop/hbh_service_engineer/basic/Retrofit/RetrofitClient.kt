package com.dollop.hbh_service_engineer.basic.Retrofit

import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.deviceName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofit: Retrofit? = null

    @JvmStatic
    val client: ApiService
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val originalRequest = chain.request()
                    val requestWithUserAgent = originalRequest.newBuilder()
                        .header("User-Agent", deviceName)
                        .build()
                    chain.proceed(requestWithUserAgent)
                })
            if (Const.Development == Constants.Debug) {
                client.addInterceptor(interceptor)
            }
            retrofit = Retrofit.Builder()
                .baseUrl(Const.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
//                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .build()
            return retrofit!!.create(ApiService::class.java)
        }

}