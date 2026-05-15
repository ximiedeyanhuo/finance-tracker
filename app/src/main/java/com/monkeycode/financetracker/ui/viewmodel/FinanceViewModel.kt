package com.monkeycode.financetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeycode.financetracker.data.model.FlowType
import com.monkeycode.financetracker.data.repository.FinanceRepository
import com.monkeycode.financetracker.data.repository.TransactionTypeRepository
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.domain.model.QueryCondition
import com.monkeycode.financetracker.domain.service.ExportService
import com.monkeycode.financetracker.domain.service.ImageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val transactionTypeRepository: TransactionTypeRepository,
    private val exportService: ExportService,
    private val imageService: ImageService
) : ViewModel() {

    private val _filterCondition = MutableStateFlow(QueryCondition())
    val filterCondition: StateFlow<QueryCondition> = _filterCondition.asStateFlow()

    private val _records = MutableStateFlow<List<FinanceRecord>>(emptyList())
    val records: StateFlow<List<FinanceRecord>> = _records.asStateFlow()

    val transactionTypesExpense = transactionTypeRepository.getByFlowTypeStream(FlowType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val transactionTypesIncome = transactionTypeRepository.getByFlowTypeStream(FlowType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _totalStats = MutableStateFlow(Pair(BigDecimal.ZERO, BigDecimal.ZERO))
    val totalStats: StateFlow<Pair<BigDecimal, BigDecimal>> = _totalStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    init {
        loadRecords()
        loadTotalStats()
    }

    fun updateFilter(condition: QueryCondition) {
        _filterCondition.value = condition
        loadRecords()
        loadFilteredStats()
    }

    fun resetFilter() {
        _filterCondition.value = QueryCondition()
        loadRecords()
        loadTotalStats()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.getRecords(_filterCondition.value)
            _isLoading.value = false
            _records.value = result
        }
    }

    private fun loadTotalStats() {
        viewModelScope.launch {
            _totalStats.value = financeRepository.getTotalStats()
        }
    }

    private fun loadFilteredStats() {
        viewModelScope.launch {
            _totalStats.value = financeRepository.getFilteredStats(_filterCondition.value)
        }
    }

    fun addRecord(record: FinanceRecord, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.addRecord(record)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    loadRecords()
                    loadTotalStats()
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "保存失败")
                }
            )
        }
    }

    fun updateRecord(record: FinanceRecord, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.updateRecord(record)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    loadRecords()
                    loadTotalStats()
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "更新失败")
                }
            )
        }
    }

    fun deleteRecord(record: FinanceRecord, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = financeRepository.deleteRecord(record)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    loadRecords()
                    loadTotalStats()
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "删除失败")
                }
            )
        }
    }

    suspend fun getRecordById(id: Long): FinanceRecord? {
        return financeRepository.getRecordById(id)
    }

    fun exportRecords(records: List<FinanceRecord>, onSuccess: (android.net.Uri) -> Unit) {
        viewModelScope.launch {
            val result = exportService.exportFinanceRecords(records)
            result.fold(
                onSuccess = { uri ->
                    onSuccess(uri)
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "导出失败")
                }
            )
        }
    }
}
