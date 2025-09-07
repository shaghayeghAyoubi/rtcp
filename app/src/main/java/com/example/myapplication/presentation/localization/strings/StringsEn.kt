package com.example.myapplication.presentation.localization.strings

object StringsEn : Strings {
    override val recognizedPeople = "Recognized People"
    override val loading = "Loading..."
    override val error: (String) -> String = { msg -> "Error: $msg" }
    override val changeLanguage = "Change Language"
    override val dashboard = "Dashboard"
    override val event = "Event"
    override val settings = "Settings"
}