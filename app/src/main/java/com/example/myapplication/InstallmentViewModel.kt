package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class InstallmentUiState(
    val price: String = "",
    val monthlyRate: Float = 1f,
    val months: Int = 12,
    val result: InstallmentResult? = null
)

class InstallmentViewModel : ViewModel() {
    var uiState by mutableStateOf(InstallmentUiState())
        private set

    fun setPrice(value: String) {
        val trimmed = value.trim()
        if (trimmed.isEmpty() || trimmed.matches(Regex("\\d*\\.?\\d*"))) {
            uiState = uiState.copy(price = trimmed, result = null)
        }
    }

    fun setRate(value: Float) {
        uiState = uiState.copy(monthlyRate = value, result = null)
    }

    fun setMonths(value: Int) {
        uiState = uiState.copy(months = value.coerceIn(1, 36), result = null)
    }

    fun calculate(): Boolean {
        val price = uiState.price.toDoubleOrNull()
        if (price == null || !price.isFinite() || price <= 0.0) return false
        uiState = uiState.copy(
            result = InstallmentCalculator.calculate(
                price = price,
                monthlyRatePercent = uiState.monthlyRate.toDouble(),
                months = uiState.months
            )
        )
        return true
    }
}
