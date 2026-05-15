package com.monkeycode.financetracker.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SQLiteDatabase
import com.monkeycode.financetracker.data.model.FlowType
import java.time.LocalDateTime

class DatabaseInitializerCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SQLiteDatabase) {
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

        val now = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        presetExpenseTypes.forEachIndexed { index, (name, sort) ->
            db.execSQL(
                "INSERT INTO transaction_type (name, flow_type, sort_order, create_time) " +
                "VALUES ('$name', 0, $sort, '$now')"
            )
        }

        presetIncomeTypes.forEachIndexed { index, (name, sort) ->
            db.execSQL(
                "INSERT INTO transaction_type (name, flow_type, sort_order, create_time) " +
                "VALUES ('$name', 1, $sort, '$now')"
            )
        }
    }
}
