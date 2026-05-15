package com.monkeycode.financetracker.data.repository

import com.monkeycode.financetracker.data.local.TransactionTypeDao
import com.monkeycode.financetracker.data.mapper.toDomain
import com.monkeycode.financetracker.data.mapper.toEntity
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionTypeRepository @Inject constructor(
    private val transactionTypeDao: TransactionTypeDao
) {

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
                flowType = type.flowType.toEntityFlowType(),
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

