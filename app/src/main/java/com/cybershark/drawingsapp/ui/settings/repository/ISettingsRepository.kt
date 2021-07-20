package com.cybershark.drawingsapp.ui.settings.repository

interface ISettingsRepository {
    suspend fun deleteAddData(): Result<Unit>
}