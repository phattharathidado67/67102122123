package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class InstallmentCalculatorTest {
    @Test
    fun flatRateCalculation_isCorrect() {
        val result = InstallmentCalculator.calculate(
            price = 12_000.0,
            monthlyRatePercent = 1.0,
            months = 12
        )

        assertEquals(1_120.0, result.monthlyPayment, 0.001)
        assertEquals(1_440.0, result.totalInterest, 0.001)
        assertEquals(13_440.0, result.totalPayment, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun infinitePrice_isRejected() {
        InstallmentCalculator.calculate(Double.POSITIVE_INFINITY, 1.0, 12)
    }
}
