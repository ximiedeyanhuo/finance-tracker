package com.monkeycode.financetracker.domain.service

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.opencsv.CSVWriter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportFinanceRecords(records: List<FinanceRecord>): Result<Uri> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "账务明细_${timestamp}.csv"
            val file = File(context.cacheDir, fileName)

            CSVWriter(FileWriter(file)).use { writer ->
                val header = arrayOf("序号", "金额", "收支类型", "交易日期", "备注")
                writer.writeNext(header)

                records.forEachIndexed { index, record ->
                    val row = arrayOf(
                        (index + 1).toString(),
                        formatAmount(record.amount),
                        record.transactionTypeName ?: "",
                        record.transactionDate.toString(),
                        record.remark ?: ""
                    )
                    writer.writeNext(row)
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportDailyStats(stats: List<Map<String, Any?>>): Result<Uri> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "日账汇总_${timestamp}.csv"
            val file = File(context.cacheDir, fileName)

            CSVWriter(FileWriter(file)).use { writer ->
                val header = arrayOf("序号", "交易日期", "收入 (元)", "支出 (元)", "结余 (元)", "收入笔数", "支出笔数")
                writer.writeNext(header)

                stats.forEachIndexed { index, stat ->
                    val row = arrayOf(
                        (index + 1).toString(),
                        stat["date"]?.toString() ?: "",
                        stat["income"]?.toString() ?: "0",
                        stat["expense"]?.toString() ?: "0",
                        stat["balance"]?.toString() ?: "0",
                        stat["incomeCount"]?.toString() ?: "0",
                        stat["expenseCount"]?.toString() ?: "0"
                    )
                    writer.writeNext(row)
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatAmount(amount: java.math.BigDecimal): String {
        val str = amount.toString()
        return if (amount.scale() <= 0) {
            amount.toPlainString()
        } else {
            str
        }
    }
}
