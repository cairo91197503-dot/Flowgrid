package com.flowgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowgrid.billing.BillingManager
import com.flowgrid.data.DataStoreManager
import com.flowgrid.data.GameResultDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val gameResultDao: GameResultDao,
    val billingManager: BillingManager
) : ViewModel() {

    val currentStreak = dataStoreManager.streakCurrent.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    
    val totalSolved = gameResultDao.getTotalSolved().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val isDailyPlayed: StateFlow<Boolean> = dataStoreManager.streakLastDate.map { lastDate ->
        val today = LocalDate.now()
        val todayInt = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
        lastDate == todayInt
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val onboardingCompleted: StateFlow<Boolean> = dataStoreManager.onboardingCompleted.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    
    fun setOnboardingCompleted() {
        viewModelScope.launch {
            dataStoreManager.setOnboardingCompleted()
        }
    }
}