package com.example.myapplication.data

import com.example.myapplication.model.Amphibian
import com.example.myapplication.network.AmphibianApiService

interface AmphibiansRepository {
    suspend fun getAmphibians(): List<Amphibian>
}

class DefaultAmphibiansRepository(
    private val amphibianApiService: AmphibianApiService
) : AmphibiansRepository {
    override suspend fun getAmphibians(): List<Amphibian> = amphibianApiService.getAmphibians()
}