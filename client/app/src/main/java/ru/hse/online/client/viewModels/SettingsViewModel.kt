package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.storage.AppDataStore
import java.util.UUID

class SettingsViewModel(private val dataStore: AppDataStore) : ViewModel() {
    fun saveUserName(name: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_NAME, name)
        }
    }

    val userName = dataStore.getValueFlow(
        AppDataStore.USER_NAME,
        defaultValue = ""
    )

    fun saveUserEmail(email: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_EMAIL, email)
        }
    }

    val userEmail = dataStore.getValueFlow(
        AppDataStore.USER_EMAIL,
        defaultValue = ""
    )

    fun saveUserPassword(password: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_PASSWORD, password)
        }
    }

    fun saveUserId(userId: UUID) {
        viewModelScope.launch {
            dataStore.saveUserId(userId)
        }
    }

    val userId = dataStore.getUserIdFlow()

    val userPassword = dataStore.getValueFlow(
        AppDataStore.USER_PASSWORD,
        defaultValue = ""
    )

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_TOKEN, token)
        }
    }

    fun saveDailyStepCount(steps: Int) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.DAILY_STEP_COUNT, steps)
        }
    }

    val dailyStepCount = dataStore.getValueFlow(
        AppDataStore.DAILY_STEP_COUNT,
        defaultValue = 6000
    )
}
