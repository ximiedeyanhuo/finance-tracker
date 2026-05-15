package com.monkeycode.financetracker.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class FinanceRecord(
    val id: Long = 0,
    val amount: BigDecimal,
    val flowType: FlowType,
    val transactionTypeId: Long,
    val transactionTypeName: String? = null,
    val transactionDate: LocalDate,
    val remark: String? = null,
    val imagePath: String? = null,
    val createTime: LocalDateTime = LocalDateTime.now(),
    val updateTime: LocalDateTime = LocalDateTime.now()
)

data class TransactionType(
    val id: Long = 0,
    val name: String,
    val flowType: FlowType,
    val sortOrder: Int = 0,
    val createTime: LocalDateTime = LocalDateTime.now()
)

data class FlowType(val value: Int) {
    companion object {
        val EXPENSE = FlowType(0)
        val INCOME = FlowType(1)

        fun fromValue(value: Int): FlowType {
            return when (value) {
                0 -> EXPENSE
                1 -> INCOME
                else -> throw IllegalArgumentException("Unknown FlowType value: $value")
            }
        }
    }
}

data class QueryCondition(
    val flowType: FlowType? = null,
    val transactionTypeId: Long? = null,
    val hasImage: Boolean? = null,
    val remarkKeyword: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

data class DailyStats(
    val date: LocalDate,
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal,
    val incomeCount: Int,
    val expenseCount: Int
)

data class WeeklyStats(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal,
    val incomeCount: Int,
    val expenseCount: Int
)

data class MonthlyStats(
    val yearMonth: String,
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal,
    val incomeCount: Int,
    val expenseCount: Int
)

data class YearlyStats(
    val year: String,
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal,
    val incomeCount: Int,
    val expenseCount: Int
)

data class CategoryStats(
    val transactionTypeId: Long,
    val transactionTypeName: String,
    val totalAmount: BigDecimal,
    val count: Int
)
