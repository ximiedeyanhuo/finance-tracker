package com.monkeycode.financetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.monkeycode.financetracker.data.converter.BigDecimalConverter
import com.monkeycode.financetracker.data.converter.FlowTypeConverter
import com.monkeycode.financetracker.data.converter.LocalDateConverter
import com.monkeycode.financetracker.data.converter.LocalDateTimeConverter
import com.monkeycode.financetracker.data.model.FinanceRecordEntity
import com.monkeycode.financetracker.data.model.TransactionTypeEntity

@Database(
    entities = [
        FinanceRecordEntity::class,
        TransactionTypeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateConverter::class,
    LocalDateTimeConverter::class,
    FlowTypeConverter::class,
    BigDecimalConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeRecordDao(): FinanceRecordDao
    abstract fun transactionTypeDao(): TransactionTypeDao

    companion object {
        const val DATABASE_NAME = "finance_tracker_db"
    }
}
