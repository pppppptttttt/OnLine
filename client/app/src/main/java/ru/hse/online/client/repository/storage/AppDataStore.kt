package ru.hse.online.client.repository.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class AppDataStore(
    private val context: Context
) {
     companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val USER_ID = stringPreferencesKey("user_id")

        val USER_TOTAL_STEPS = intPreferencesKey("user_total_steps")
        val USER_TOTAL_CALORIES = doublePreferencesKey("user_total_calories")
        val USER_TOTAL_DISTANCE = doublePreferencesKey("user_total_distance")
        val USER_TOTAL_TIME = longPreferencesKey("user_total_time")

        val USER_ONLINE_STEPS = intPreferencesKey("user_online_steps")
        val USER_ONLINE_CALORIES = doublePreferencesKey("user_online_calories")
        val USER_ONLINE_DISTANCE = doublePreferencesKey("user_online_distance")
        val USER_ONLINE_TIME = longPreferencesKey("user_online_time")

        val DAILY_STEP_GOAL = intPreferencesKey("daily_step_count")
    }

    suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> getValueFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    fun getUserIdFlow(): Flow<UUID> {
        return getValueFlow(USER_ID, "00000000-0000-0000-0000-000000000000")
            .map { uuidString ->
                UUID.fromString(uuidString)
            }
    }

    suspend fun saveCredentials(token: String, userId: UUID, email: String, name: String, password: String) {
        saveValue(USER_TOKEN, token)
        saveValue(USER_ID, userId.toString())
        saveValue(USER_EMAIL, email)
        saveValue(USER_NAME, name)
        saveValue(USER_PASSWORD, password)
    }
}
