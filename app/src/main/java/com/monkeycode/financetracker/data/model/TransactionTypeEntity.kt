package com.monkeycode.financetracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.monkeycode.financetracker.data.converter.FlowTypeConverter
import com.monkeycode.financetracker.data.converter.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity(tableName = "transaction_type")
data class TransactionTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "flow_type")
    @TypeConverters(FlowTypeConverter::class)
    val flowType: FlowType,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "create_time")
    @TypeConverters(LocalDateTimeConverter::class)
    val createTime: LocalDateTime = LocalDateTime.now()
)
