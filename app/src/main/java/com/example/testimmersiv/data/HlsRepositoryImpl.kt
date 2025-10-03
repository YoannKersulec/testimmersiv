package com.example.testimmersiv.data

import com.example.testimmersiv.domain.HlsRepository
import com.example.testimmersiv.domain.VideoSample

class HlsRepositoryImpl(
    private val api: HlsApi
) : HlsRepository {
    override suspend fun getVids(): List<VideoSample> {
        return api.getVids() // TODO Mapping to other data if needed
    }
}