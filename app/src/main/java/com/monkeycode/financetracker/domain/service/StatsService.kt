package com.monkeycode.financetracker.domain.service

import com.monkeycode.financetracker.data.repository.FinanceRepository
import com.monkeycode.financetracker.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsService @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    suspend fun getDailyStats(startDate: LocalDate, endDate: LocalDate): List<DailyStats> = withContext(Dispatchers.IO) {
        val allRecords = financeRepository.getRecords(
            QueryCondition(startDate = startDate, endDate = endDate)
        )
        
        val statsMap = mutableMapOf<LocalDate, DailyStatsBuilder>()
        
        // 初始化所有日期
        var current = startDate
        while (!current.isAfter(endDate)) {
            statsMap[current] = DailyStatsBuilder(current)
            current = current.plusDays(1)
        }
        
        // 聚合数据
        allRecords.forEach { record ->
            val builder = statsMap.getOrPut(record.transactionDate) { 
                DailyStatsBuilder(record.transactionDate) 
            }
            if (record.flowType == FlowType.INCOME) {
                builder.addIncome(record.amount)
            } else {
                builder.addExpense(record.amount)
            }
        }
        
        statsMap.values.map { it.build() }.sortedByDescending { it.date }
    }

    suspend fun getWeeklyStats(referenceDate: LocalDate): List<WeeklyStats> = withContext(Dispatchers.IO) {
        val allRecords = financeRepository.getRecords(QueryCondition())
        
        val weekMap = mutableMapOf<Pair<LocalDate, LocalDate>, WeeklyStatsBuilder>()
        
        allRecords.forEach { record ->
            val monday = record.transactionDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = record.transactionDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            val key = Pair(monday, sunday)
            
            val builder = weekMap.getOrPut(key) { 
                WeeklyStatsBuilder(monday, sunday) 
            }
            if (record.flowType == FlowType.INCOME) {
                builder.addIncome(record.amount)
            } else {
                builder.addExpense(record.amount)
            }
        }
        
        weekMap.values.map { it.build() }.sortedByDescending { it.startDate }
    }

    suspend fun getMonthlyStats(startMonth: YearMonth, endMonth: YearMonth): List<MonthlyStats> = withContext(Dispatchers.IO) {
        val allRecords = financeRepository.getRecords(
            QueryCondition(
                startDate = startMonth.atDay(1),
                endDate = endMonth.atEndOfMonth()
            )
        )
        
        val statsMap = mutableMapOf<String, MonthlyStatsBuilder>()
        
        var current = startMonth
        while (!current.isAfter(endMonth)) {
            val key = current.toString()
            statsMap[key] = MonthlyStatsBuilder(key)
            current = current.plusMonths(1)
        }
        
        allRecords.forEach { record ->
            val key = YearMonth.from(record.transactionDate).toString()
            val builder = statsMap.getOrPut(key) { 
                MonthlyStatsBuilder(key) 
            }
            if (record.flowType == FlowType.INCOME) {
                builder.addIncome(record.amount)
            } else {
                builder.addExpense(record.amount)
            }
        }
        
        statsMap.values.map { it.build() }.sortedByDescending { it.yearMonth }
    }

    suspend fun getYearlyStats(startYear: Int, endYear: Int): List<YearlyStats> = withContext(Dispatchers.IO) {
        val allRecords = financeRepository.getRecords(
            QueryCondition(
                startDate = LocalDate.of(startYear, 1, 1),
                endDate = LocalDate.of(endYear, 12, 31)
            )
        )
        
        val statsMap = mutableMapOf<String, YearlyStatsBuilder>()
        
        for (year in startYear..endYear) {
            val key = year.toString()
            statsMap[key] = YearlyStatsBuilder(key)
        }
        
        allRecords.forEach { record ->
            val key = record.transactionDate.year.toString()
            val builder = statsMap.getOrPut(key) { 
                YearlyStatsBuilder(key) 
            }
            if (record.flowType == FlowType.INCOME) {
                builder.addIncome(record.amount)
            } else {
                builder.addExpense(record.amount)
            }
        }
        
        statsMap.values.map { it.build() }.sortedByDescending { it.year }
    }

    suspend fun getMonthDailyStats(yearMonth: YearMonth): List<DailyStats> = withContext(Dispatchers.IO) {
        getDailyStats(yearMonth.atDay(1), yearMonth.atEndOfMonth())
    }

    suspend fun getCategoryStats(
        startDate: LocalDate,
        endDate: LocalDate,
        flowType: FlowType
    ): List<CategoryStats> = withContext(Dispatchers.IO) {
        val allRecords = financeRepository.getRecords(
            QueryCondition(
                startDate = startDate,
                endDate = endDate,
                flowType = flowType
            )
        )
        
        val statsMap = mutableMapOf<Long, CategoryStatsBuilder>()
        
        allRecords.forEach { record ->
            val builder = statsMap.getOrPut(record.transactionTypeId) {
                CategoryStatsBuilder(record.transactionTypeId, record.transactionTypeName ?: "未分类")
            }
            builder.addAmount(record.amount)
        }
        
        statsMap.values.map { it.build() }.sortedByDescending { it.totalAmount }
    }
}

// Helper classes
private class DailyStatsBuilder(val date: LocalDate) {
    private var income = BigDecimal.ZERO
    private var expense = BigDecimal.ZERO
    private var incomeCount = 0
    private var expenseCount = 0
    
    fun addIncome(amount: BigDecimal) {
        income += amount
        incomeCount++
    }
    
    fun addExpense(amount: BigDecimal) {
        expense += amount
        expenseCount++
    }
    
    fun build() = DailyStats(
        date = date,
        income = income,
        expense = expense,
        balance = income - expense,
        incomeCount = incomeCount,
        expenseCount = expenseCount
    )
}

private class WeeklyStatsBuilder(val startDate: LocalDate, val endDate: LocalDate) {
    private var income = BigDecimal.ZERO
    private var expense = BigDecimal.ZERO
    private var incomeCount = 0
    private var expenseCount = 0
    
    fun addIncome(amount: BigDecimal) {
        income += amount
        incomeCount++
    }
    
    fun addExpense(amount: BigDecimal) {
        expense += amount
        expenseCount++
    }
    
    fun build() = WeeklyStats(
        startDate = startDate,
        endDate = endDate,
        income = income,
        expense = expense,
        balance = income - expense,
        incomeCount = incomeCount,
        expenseCount = expenseCount
    )
}

private class MonthlyStatsBuilder(val yearMonth: String) {
    private var income = BigDecimal.ZERO
    private var expense = BigDecimal.ZERO
    private var incomeCount = 0
    private var expenseCount = 0
    
    fun addIncome(amount: BigDecimal) {
        income += amount
        incomeCount++
    }
    
    fun addExpense(amount: BigDecimal) {
        expense += amount
        expenseCount++
    }
    
    fun build() = MonthlyStats(
        yearMonth = yearMonth,
        income = income,
        expense = expense,
        balance = income - expense,
        incomeCount = incomeCount,
        expenseCount = expenseCount
    )
}

private class YearlyStatsBuilder(val year: String) {
    private var income = BigDecimal.ZERO
    private var expense = BigDecimal.ZERO
    private var incomeCount = 0
    private var expenseCount = 0
    
    fun addIncome(amount: BigDecimal) {
        income += amount
        incomeCount++
    }
    
    fun addExpense(amount: BigDecimal) {
        expense += amount
        expenseCount++
    }
    
    fun build() = YearlyStats(
        year = year,
        income = income,
        expense = expense,
        balance = income - expense,
        incomeCount = incomeCount,
        expenseCount = expenseCount
    )
}

private class CategoryStatsBuilder(
    val transactionTypeId: Long,
    val transactionTypeName: String
) {
    private var totalAmount = BigDecimal.ZERO
    private var count = 0
    
    fun addAmount(amount: BigDecimal) {
        totalAmount += amount
        count++
    }
    
    fun build() = CategoryStats(
        transactionTypeId = transactionTypeId,
        transactionTypeName = transactionTypeName,
        totalAmount = totalAmount,
        count = count
    )
}
