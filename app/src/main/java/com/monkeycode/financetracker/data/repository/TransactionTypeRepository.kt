package com.monkeycode.financetracker.data.repository

import com.monkeycode.financetracker.data.local.TransactionTypeDao
import com.monkeycode.financetracker.data.mapper.toDomain
import com.monkeycode.financetracker.data.mapper.toEntity
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.data.model.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionTypeRepository @Inject constructor(
    private val transactionTypeDao: TransactionTypeDao
) {
    private var isInitialized = false

    suspend fun ensureInitialized() {
        if (isInitialized) return
        
        val count = transactionTypeDao.getCount()
        if (count == 0L) {
            insertPresetTypes()
        }
        isInitialized = true
    }

    private suspend fun insertPresetTypes() {
        val now = LocalDateTime.now()
        
        val presetExpenseTypes = listOf(
            "日常消费" to 0,
            "房贷" to 1,
            "借呗" to 2,
            "花呗" to 3,
            "京东白条" to 4,
            "信用卡 - 平安" to 5,
            "信用卡 - 邮储" to 6,
            "投资理财" to 7,
            "人情往来" to 8
        )

        val presetIncomeTypes = listOf(
            "工资" to 0,
            "转账" to 1,
            "理财赎回" to 2,
            "退税" to 3
        )

        presetExpenseTypes.forEach { (name, sort) ->
            transactionTypeDao.insert(
                TransactionTypeEntity(
                    name = name,
                    flowType = FlowType.EXPENSE,
                    sortOrder = sort,
                    createTime = now
                )
            )
        }

        presetIncomeTypes.forEach { (name, sort) ->
            transactionTypeDao.insert(
                TransactionTypeEntity(
                    name = name,
                    flowType = FlowType.INCOME,
                    sortOrder = sort,
                    createTime = now
                )
            )
        }
    }

    fun getByFlowTypeStream(flowType: FlowType): Flow<List<TransactionType>> {
        return transactionTypeDao.getByFlowType(flowType).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getAllStream(): Flow<List<TransactionType>> {
        return transactionTypeDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getTypeById(id: Long): TransactionType? {
        return transactionTypeDao.getById(id)?.toDomain()
    }

    suspend fun addType(type: TransactionType): Result<Long> {
        return try {
            val duplicateCount = transactionTypeDao.checkDuplicateName(
                flowType = type.flowType,
                name = type.name
            )
            if (duplicateCount > 0) {
                return Result.failure(IllegalStateException("该类型名称已存在"))
            }
            val id = transactionTypeDao.insert(type.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateType(type: TransactionType): Result<Unit> {
        return try {
            val duplicateCount = transactionTypeDao.checkDuplicateName(
                flowType = type.flowType,
                name = type.name,
                excludeId = type.id
            )
            if (duplicateCount > 0) {
                return Result.failure(IllegalStateException("该类型名称已存在"))
            }
            transactionTypeDao.update(type.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteType(type: TransactionType): Result<Unit> {
        return try {
            val usageCount = transactionTypeDao.getUsageCount(type.id)
            if (usageCount > 0) {
                return Result.failure(IllegalStateException("该类型已被使用，无法删除"))
            }
            transactionTypeDao.delete(type.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

