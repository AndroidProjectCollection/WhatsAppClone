package com.febrian.whatsappclone.api

import com.febrian.whatsappclone.utils.Constant.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiNotification by lazy {
        retrofit.create(ApiNotification::class.java)
    }
}