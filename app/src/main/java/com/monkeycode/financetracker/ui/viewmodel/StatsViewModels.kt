package com.monkeycode.financetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeycode.financetracker.data.repository.FinanceRepository
import com.monkeycode.financetracker.domain.model.CategoryStats
import com.monkeycode.financetracker.domain.model.DailyStats
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.MonthlyStats
import com.monkeycode.financetracker.domain.model.WeeklyStats
import com.monkeycode.financetracker.domain.model.YearlyStats
import com.monkeycode.financetracker.domain.service.StatsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DailyStatsViewModel @Inject constructor(
    private val statsService: StatsService
) : ViewModel() {

    private val _dailyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val dailyStats: StateFlow<List<DailyStats>> = _dailyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            val stats = statsService.getDailyStats(startDate, endDate)
            _isLoading.value = false
            _dailyStats.value = stats
        }
    }
}

@HiltViewModel
class WeeklyStatsViewModel @Inject constructor(
    private val statsService: StatsService
) : ViewModel() {

    private val _weeklyStats = MutableStateFlow<List<WeeklyStats>>(emptyList())
    val weeklyStats: StateFlow<List<WeeklyStats>> = _weeklyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats(referenceDate: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            val stats = statsService.getWeeklyStats(referenceDate)
            _isLoading.value = false
            _weeklyStats.value = stats
        }
    }
}

@HiltViewModel
class MonthlyStatsViewModel @Inject constructor(
    private val statsService: StatsService
) : ViewModel() {

    private val _monthlyStats = MutableStateFlow<List<MonthlyStats>>(emptyList())
    val monthlyStats: StateFlow<List<MonthlyStats>> = _monthlyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats(startMonth: YearMonth, endMonth: YearMonth) {
        viewModelScope.launch {
            _isLoading.value = true
            val stats = statsService.getMonthlyStats(startMonth, endMonth)
            _isLoading.value = false
            _monthlyStats.value = stats
        }
    }
}

@HiltViewModel
class YearlyStatsViewModel @Inject constructor(
    private val statsService: StatsService
) : ViewModel() {

    private val _yearlyStats = MutableStateFlow<List<YearlyStats>>(emptyList())
    val yearlyStats: StateFlow<List<YearlyStats>> = _yearlyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats(startYear: Int, endYear: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val stats = statsService.getYearlyStats(startYear, endYear)
            _isLoading.value = false
            _yearlyStats.value = stats
        }
    }
}

@HiltViewModel
class CategoryStatsViewModel @Inject constructor(
    private val statsService: StatsService
) : ViewModel() {

    private val _categoryStats = MutableStateFlow<List<CategoryStats>>(emptyList())
    val categoryStats: StateFlow<List<CategoryStats>> = _categoryStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadStats(startDate: LocalDate, endDate: LocalDate, flowType: FlowType) {
        viewModelScope.launch {
            _isLoading.value = true
            val stats = statsService.getCategoryStats(startDate, endDate, flowType)
            _isLoading.value = false
            _categoryStats.value = stats
        }
    }
}
