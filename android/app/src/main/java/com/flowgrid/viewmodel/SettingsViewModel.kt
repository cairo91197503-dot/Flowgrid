package com.flowgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowgrid.billing.BillingManager
import com.flowgrid.data.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    val billingManager: BillingManager
) : ViewModel() {

    val daltonicMode = dataStoreManager.daltonicMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    
    val isPro: StateFlow<Boolean> = billingManager.isPro
    
    val streakCurrent = dataStoreManager.streakCurrent.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    
    val streakBest = dataStoreManager.streakBest.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    fun setDaltonicMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDaltonicMode(enabled)
        }
    }
}
