package com.monkeycode.financetracker.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.monkeycode.financetracker.domain.model.FlowType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseInitializerCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
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

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val insertStatement = db.compileStatement(
            "INSERT INTO transaction_type (name, flow_type, sort_order, create_time) VALUES (?, ?, ?, ?)"
        )

        presetExpenseTypes.forEach { (name, sort) ->
            insertStatement.bindString(1, name)
            insertStatement.bindLong(2, FlowType.EXPENSE.value.toLong())
            insertStatement.bindLong(3, sort.toLong())
            insertStatement.bindString(4, now)
            insertStatement.executeInsert()
        }

        presetIncomeTypes.forEach { (name, sort) ->
            insertStatement.bindString(1, name)
            insertStatement.bindLong(2, FlowType.INCOME.value.toLong())
            insertStatement.bindLong(3, sort.toLong())
            insertStatement.bindString(4, now)
            insertStatement.executeInsert()
        }

        insertStatement.close()
    }
}
