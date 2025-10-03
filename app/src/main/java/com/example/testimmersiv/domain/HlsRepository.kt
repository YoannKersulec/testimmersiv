package com.example.testimmersiv.domain

interface HlsRepository {
    suspend fun getVids(): List<VideoSample>
}