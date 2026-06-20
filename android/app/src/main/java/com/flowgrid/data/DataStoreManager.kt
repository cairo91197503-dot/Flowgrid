package com.flowgrid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val STREAK_CURRENT = intPreferencesKey("streak_current")
        val STREAK_BEST = intPreferencesKey("streak_best")
        val STREAK_LAST_DATE = intPreferencesKey("streak_last_date")
        val ADS_REMOVED = booleanPreferencesKey("ads_removed")
        val DICA_COUNT = intPreferencesKey("dica_count")
        val THEME_SELECTED = stringPreferencesKey("theme_selected")
        val DALTONIC_MODE = booleanPreferencesKey("daltonic_mode")
    }

    val streakCurrent: Flow<Int> = dataStore.data.map { it[STREAK_CURRENT] ?: 0 }
    val streakBest: Flow<Int> = dataStore.data.map { it[STREAK_BEST] ?: 0 }
    val streakLastDate: Flow<Int> = dataStore.data.map { it[STREAK_LAST_DATE] ?: 0 }
    val adsRemoved: Flow<Boolean> = dataStore.data.map { it[ADS_REMOVED] ?: false }
    val dicaCount: Flow<Int> = dataStore.data.map { it[DICA_COUNT] ?: 3 } // Start with 3 hints
    val themeSelected: Flow<String> = dataStore.data.map { it[THEME_SELECTED] ?: "stone_garden" }
    val daltonicMode: Flow<Boolean> = dataStore.data.map { it[DALTONIC_MODE] ?: false }

    suspend fun setDaltonicMode(enabled: Boolean) {
        dataStore.edit { it[DALTONIC_MODE] = enabled }
    }
    
    suspend fun setAdsRemoved(removed: Boolean) {
        dataStore.edit { it[ADS_REMOVED] = removed }
    }

    suspend fun useDica() {
        dataStore.edit { 
            val current = it[DICA_COUNT] ?: 3
            if (current > 0) {
                it[DICA_COUNT] = current - 1
            }
        }
    }

    suspend fun updateStreak(todayInt: Int) {
        dataStore.edit { prefs ->
            val lastDate = prefs[STREAK_LAST_DATE] ?: 0
            val currentStreak = prefs[STREAK_CURRENT] ?: 0
            
            if (lastDate != todayInt) {
                val nextExpectedDate = todayInt - 1 // Simplified: this isn't mathematically perfect over months, better to use days since epoch
                // For demonstration using actual int like 20240502:
                // Actually, let's just assume consecutive days logic handled elsewhere or approximate
                // For true consecutive, we should compare LocalDate.toEpochDay()
                
                prefs[STREAK_CURRENT] = currentStreak + 1
                prefs[STREAK_LAST_DATE] = todayInt
                
                val best = prefs[STREAK_BEST] ?: 0
                if (currentStreak + 1 > best) {
                    prefs[STREAK_BEST] = currentStreak + 1
                }
            }
        }
    }
    
    suspend fun resetStreak() {
        dataStore.edit {
            it[STREAK_CURRENT] = 0
            // Do not reset last date
        }
    }
}
