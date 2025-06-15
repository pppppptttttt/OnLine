package ru.hse.online.client.viewModels

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

    val userWeight = dataStore.getValueFlow(
        AppDataStore.USER_WEIGHT,
        defaultValue = 0
    )
    val userHeight = dataStore.getValueFlow(
        AppDataStore.USER_HEIGHT,
        defaultValue = 0
    )
    val userGender = dataStore.getValueFlow(
        AppDataStore.USER_GENDER,
        defaultValue = ""
    )

    val dailyStepGoal = dataStore.getValueFlow(
        AppDataStore.USER_DAILY_GOAL,
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
        viewModelScope.launch {
            val value = steps.toIntOrNull() ?: 0
            dataStore.saveValue(AppDataStore.USER_DAILY_GOAL, value)
        }
    }

    fun saveUserWeight(weight: String) {
        viewModelScope.launch {
            val value = weight.toIntOrNull() ?: 0
            dataStore.saveValue(AppDataStore.USER_WEIGHT, value)
        }
    }

    fun saveUserHeight(height: String) {
        viewModelScope.launch {
            val value = height.toIntOrNull() ?: 0
            dataStore.saveValue(AppDataStore.USER_HEIGHT, value)
        }
    }

    fun saveUserGender(gender: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_GENDER, gender)
        }
    }
}