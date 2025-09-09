package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.BaseUrlRepository
import javax.inject.Inject

class SaveBaseUrlUseCase @Inject constructor(
    private val repository: BaseUrlRepository
) {
    suspend operator fun invoke(url: String) = repository.saveBaseUrl(url)
}