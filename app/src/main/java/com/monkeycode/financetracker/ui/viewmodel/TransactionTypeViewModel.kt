package com.monkeycode.financetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.data.repository.TransactionTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionTypeViewModel @Inject constructor(
    private val transactionTypeRepository: TransactionTypeRepository
) : ViewModel() {

    val allTypes = transactionTypeRepository.getAllStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val typesExpense = transactionTypeRepository.getByFlowTypeStream(FlowType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val typesIncome = transactionTypeRepository.getByFlowTypeStream(FlowType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun addType(type: TransactionType, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionTypeRepository.addType(type)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "添加失败")
                }
            )
        }
    }

    fun updateType(type: TransactionType, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionTypeRepository.updateType(type)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "更新失败")
                }
            )
        }
    }

    fun deleteType(type: TransactionType, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = transactionTypeRepository.deleteType(type)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { error ->
                    _error.emit(error.message ?: "删除失败")
                }
            )
        }
    }
}
