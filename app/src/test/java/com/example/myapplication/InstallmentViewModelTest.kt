package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InstallmentViewModelTest {
    @Test
    fun calculate_updatesUiStateWithCorrectResult() {
        val viewModel = InstallmentViewModel()
        viewModel.setPrice("12000")
        viewModel.setRate(1f)
        viewModel.setMonths(12)

        assertTrue(viewModel.calculate())
        val result = viewModel.uiState.result
        assertNotNull(result)
        assertEquals(1_120.0, result!!.monthlyPayment, 0.001)
        assertEquals(1_440.0, result.totalInterest, 0.001)
        assertEquals(13_440.0, result.totalPayment, 0.001)
    }
}
