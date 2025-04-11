package ru.hse.online.client.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.storage.AppDataStore

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

    val userPassword = dataStore.getValueFlow(
        AppDataStore.USER_PASSWORD,
        defaultValue = ""
    )

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            dataStore.saveValue(AppDataStore.USER_TOKEN, token)
        }
    }

    val userUUID = dataStore.getValueFlow(
        AppDataStore.USER_TOKEN,
        defaultValue = ""
    )
}
