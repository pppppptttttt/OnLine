package ru.hse.online.client.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.storage.AppDataStore
import java.util.UUID

class SettingsViewModel(private val dataStore: AppDataStore) : ViewModel() {
    val userName = dataStore.getValueFlow(
        AppDataStore.USER_NAME,
        defaultValue = ""
    )
    val userEmail = dataStore.getValueFlow(
        AppDataStore.USER_EMAIL,
        defaultValue = ""
    )
    val userPassword = dataStore.getValueFlow(
        AppDataStore.USER_PASSWORD,
        defaultValue = ""
    )
    val token = dataStore.getValueFlow(
        AppDataStore.USER_TOKEN,
        defaultValue = ""
    )
    val userId = dataStore.getUserIdFlow()
    val dailyStepGoal = dataStore.getValueFlow(
        AppDataStore.DAILY_STEP_GOAL,
        defaultValue = 6000
    )

    fun saveUserName(name: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_NAME, name)
        }
    }


    fun saveUserEmail(email: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_EMAIL, email)
        }
    }

    fun saveUserPassword(password: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_PASSWORD, password)
        }
    }

    fun saveUserId(userId: UUID) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_ID, userId.toString())
        }
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_TOKEN, token)
        }
    }

    fun saveDailyStepGoal(steps: String) {
        if (steps.isNotBlank() && steps.isDigitsOnly()) {
            viewModelScope.launch {
                dataStore.saveValue(AppDataStore.DAILY_STEP_GOAL, steps.toInt())
            }
        }
    }
}
