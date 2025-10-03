package com.example.testimmersiv.data

import com.example.testimmersiv.domain.VideoSample
import retrofit2.http.GET

interface HlsApi {
    @GET("videos.json")
    suspend fun getVids(): List<VideoSample>
}