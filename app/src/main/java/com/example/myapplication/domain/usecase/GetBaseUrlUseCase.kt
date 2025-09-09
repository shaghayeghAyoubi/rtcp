package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.BaseUrlRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetBaseUrlUseCase @Inject constructor(
    private val repository: BaseUrlRepository
) {
    operator fun invoke(): Flow<String?> = repository.getBaseUrl()
}