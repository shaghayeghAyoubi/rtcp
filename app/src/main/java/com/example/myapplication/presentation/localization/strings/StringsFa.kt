package com.example.myapplication.presentation.localization.strings

object StringsFa : Strings {
    override val recognizedPeople = "افراد شناسایی شده"
    override val loading = "در حال بارگذاری..."
    override val error: (String) -> String = { msg -> "خطا: $msg" }
    override val changeLanguage = "تغییر زبان"
    override val dashboard = "داشبورد"
    override val event = "رویداد"
    override val settings = "تنظیمات"
}