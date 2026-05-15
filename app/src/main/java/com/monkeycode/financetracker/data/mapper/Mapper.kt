package com.monkeycode.financetracker.data.mapper

import com.monkeycode.financetracker.data.model.FinanceRecordEntity
import com.monkeycode.financetracker.data.model.TransactionTypeEntity
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.domain.model.TransactionType
import java.math.BigDecimal

fun FinanceRecordEntity.toDomain(): FinanceRecord {
    return FinanceRecord(
        id = id,
        amount = amount,
        flowType = flowType,
        transactionTypeId = transactionTypeId,
        transactionDate = transactionDate,
        remark = remark,
        imagePath = imagePath,
        createTime = createTime,
        updateTime = updateTime
    )
}

fun FinanceRecord.toEntity(): FinanceRecordEntity {
    return FinanceRecordEntity(
        id = id,
        amount = amount,
        flowType = flowType,
        transactionTypeId = transactionTypeId,
        transactionDate = transactionDate,
        remark = remark,
        imagePath = imagePath,
        createTime = createTime,
        updateTime = updateTime
    )
}

fun TransactionTypeEntity.toDomain(): TransactionType {
    return TransactionType(
        id = id,
        name = name,
        flowType = flowType,
        sortOrder = sortOrder,
        createTime = createTime
    )
}

fun TransactionType.toEntity(): TransactionTypeEntity {
    return TransactionTypeEntity(
        id = id,
        name = name,
        flowType = flowType,
        sortOrder = sortOrder,
        createTime = createTime
    )
}

fun BigDecimal?.toSafeBigDecimal(): BigDecimal = this ?: BigDecimal.ZERO

fun Double?.toBigDecimal(): BigDecimal {
    return this?.toBigDecimal() ?: BigDecimal.ZERO
}
