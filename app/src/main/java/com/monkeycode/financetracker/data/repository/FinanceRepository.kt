package com.monkeycode.financetracker.data.repository

import com.monkeycode.financetracker.data.mapper.toDomain
import com.monkeycode.financetracker.data.mapper.toEntity
import com.monkeycode.financetracker.data.mapper.toSafeBigDecimal
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.QueryCondition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val financeRecordDao: com.monkeycode.financetracker.data.local.FinanceRecordDao
) {

    suspend fun getRecords(condition: QueryCondition): List<FinanceRecord> {
        return financeRecordDao.getRecords(
            flowType = condition.flowType,
            transactionTypeId = condition.transactionTypeId,
            hasImage = condition.hasImage,
            remarkKeyword = condition.remarkKeyword,
            startDate = condition.startDate,
            endDate = condition.endDate
        ).map { it.toDomain() }
    }

    suspend fun getRecordById(id: Long): FinanceRecord? {
        return financeRecordDao.getById(id)?.toDomain()
    }

    suspend fun addRecord(record: FinanceRecord): Result<Long> {
        return try {
            if (record.amount <= BigDecimal.ZERO) {
                return Result.failure(IllegalArgumentException("金额必须大于 0"))
            }
            val id = financeRecordDao.insert(record.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRecord(record: FinanceRecord): Result<Unit> {
        return try {
            if (record.amount <= BigDecimal.ZERO) {
                return Result.failure(IllegalArgumentException("金额必须大于 0"))
            }
            financeRecordDao.update(record.toEntity().copy(updateTime = java.time.LocalDateTime.now()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecord(record: FinanceRecord): Result<Unit> {
        return try {
            financeRecordDao.delete(record.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTotalStats(): Pair<BigDecimal, BigDecimal> {
        val result = financeRecordDao.getTotalStats()
        val income = result.income?.toBigDecimal() ?: BigDecimal.ZERO
        val expense = result.expense?.toBigDecimal() ?: BigDecimal.ZERO
        return Pair(income, expense)
    }

    suspend fun getFilteredStats(condition: QueryCondition): Pair<BigDecimal, BigDecimal> {
        val result = financeRecordDao.getFilteredStats(
            flowType = condition.flowType,
            transactionTypeId = condition.transactionTypeId,
            hasImage = condition.hasImage,
            remarkKeyword = condition.remarkKeyword,
            startDate = condition.startDate,
            endDate = condition.endDate
        )
        val income = result.income?.toBigDecimal() ?: BigDecimal.ZERO
        val expense = result.expense?.toBigDecimal() ?: BigDecimal.ZERO
        return Pair(income, expense)
    }
}
