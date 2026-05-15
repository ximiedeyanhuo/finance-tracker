package com.monkeycode.financetracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.monkeycode.financetracker.data.converter.FlowTypeConverter
import com.monkeycode.financetracker.data.converter.LocalDateConverter
import com.monkeycode.financetracker.data.converter.LocalDateTimeConverter
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "finance_record")
data class FinanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "amount")
    val amount: BigDecimal,

    @ColumnInfo(name = "flow_type")
    @TypeConverters(FlowTypeConverter::class)
    val flowType: FlowType,

    @ColumnInfo(name = "transaction_type_id")
    val transactionTypeId: Long,

    @ColumnInfo(name = "transaction_date")
    @TypeConverters(LocalDateConverter::class)
    val transactionDate: LocalDate,

    @ColumnInfo(name = "remark")
    val remark: String? = null,

    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,

    @ColumnInfo(name = "create_time")
    @TypeConverters(LocalDateTimeConverter::class)
    val createTime: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "update_time")
    @TypeConverters(LocalDateTimeConverter::class)
    val updateTime: LocalDateTime = LocalDateTime.now()
)
