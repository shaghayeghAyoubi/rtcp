package com.example.myapplication.presentation.localization

import com.example.myapplication.data.repository.LanguageRepositoryImpl
import com.example.myapplication.presentation.localization.strings.Strings
import com.example.myapplication.presentation.localization.strings.StringsEn
import com.example.myapplication.presentation.localization.strings.StringsFa

import kotlinx.coroutines.flow.map
import com.example.myapplication.domain.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class LocalizationProvider @Inject constructor(
    private val repository: LanguageRepositoryImpl
) {
    val stringsFlow: Flow<Strings> =
        repository.getLanguage().map { lang ->
            when (lang) {
                Language.EN -> StringsEn
                Language.FA -> StringsFa
            }
        }

    suspend fun changeLanguage(language: Language) {
        repository.saveLanguage(language)
    }
}