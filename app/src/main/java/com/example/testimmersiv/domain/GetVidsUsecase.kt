package com.example.testimmersiv.domain

class GetVidsUsecase(
    private val repository: HlsRepository
) {
    suspend operator fun invoke() : List<VideoSample> {
        return repository.getVids()
    }
}