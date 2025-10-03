package com.example.testimmersiv

import com.example.testimmersiv.data.HlsApi
import com.example.testimmersiv.data.HlsRepositoryImpl
import com.example.testimmersiv.domain.GetVidsUsecase
import com.example.testimmersiv.domain.HlsRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://hls-video-samples.r2.immersiv.cloud/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(HlsApi::class.java)

    private val repository: HlsRepository = HlsRepositoryImpl(api)
    val getPostsUseCase = GetVidsUsecase(repository)
}