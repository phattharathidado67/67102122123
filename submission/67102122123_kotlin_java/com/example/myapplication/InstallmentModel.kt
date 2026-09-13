package com.example.myapplication

data class InstallmentResult(
    val monthlyPayment: Double,
    val totalInterest: Double,
    val totalPayment: Double
)

object InstallmentCalculator {
    fun calculate(price: Double, monthlyRatePercent: Double, months: Int): InstallmentResult {
        require(price.isFinite() && price > 0.0)
        require(monthlyRatePercent.isFinite() && monthlyRatePercent >= 0.0)
        require(months > 0)

        val totalInterest = price * (monthlyRatePercent / 100.0) * months
        val totalPayment = price + totalInterest
        return InstallmentResult(
            monthlyPayment = totalPayment / months,
            totalInterest = totalInterest,
            totalPayment = totalPayment
        )
    }
}
