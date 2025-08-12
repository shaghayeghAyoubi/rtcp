package com.example.myapplication.domain.usecase


import com.example.myapplication.domain.model.RecognizedPerson
import com.example.myapplication.domain.repository.SurveillanceRepository

class GetRecognizedPeopleUseCase(
    private val repository: SurveillanceRepository
) {
    suspend operator fun invoke(
        pageNumber: Int,
        pageSize: Int,
        sort: String? = null,
        creationDateFrom: String? = null,
        creationDateTo: String? = null
    ): List<RecognizedPerson> {
        return repository.getRecognizedPeople(
            pageNumber = pageNumber,
            pageSize = pageSize,
            sort = sort,
            creationDateFrom = creationDateFrom,
            creationDateTo = creationDateTo
        )
    }
}