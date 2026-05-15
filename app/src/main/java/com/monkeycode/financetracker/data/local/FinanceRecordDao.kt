package com.monkeycode.financetracker.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.monkeycode.financetracker.data.model.FinanceRecordEntity
import com.monkeycode.financetracker.data.model.FlowType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FinanceRecordDao {

    @Query("""
        SELECT * FROM finance_record 
        WHERE (:flowType IS NULL OR flow_type = :flowType)
        AND (:transactionTypeId IS NULL OR transaction_type_id = :transactionTypeId)
        AND (:hasImage IS NULL OR (:hasImage = 1 AND image_path IS NOT NULL) OR (:hasImage = 0 AND image_path IS NULL))
        AND (:remarkKeyword IS NULL OR remark LIKE '%' || :remarkKeyword || '%')
        AND (:startDate IS NULL OR transaction_date >= :startDate)
        AND (:endDate IS NULL OR transaction_date <= :endDate)
        ORDER BY transaction_date DESC, id DESC
    """)
    fun getRecords(
        flowType: FlowType? = null,
        transactionTypeId: Long? = null,
        hasImage: Boolean? = null,
        remarkKeyword: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): List<FinanceRecordEntity>

    @Query("SELECT * FROM finance_record WHERE id = :id")
    suspend fun getById(id: Long): FinanceRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: FinanceRecordEntity): Long

    @Update
    suspend fun update(record: FinanceRecordEntity)

    @Delete
    suspend fun delete(record: FinanceRecordEntity)

    @Query("SELECT COUNT(*) FROM finance_record")
    suspend fun getCount(): Int

    @Query("""
        SELECT SUM(CASE WHEN flow_type = 1 THEN amount ELSE 0 END) as income,
               SUM(CASE WHEN flow_type = 0 THEN amount ELSE 0 END) as expense
        FROM finance_record
    """)
    suspend fun getTotalStats(): Map<String, Double?>

    @Query("""
        SELECT SUM(CASE WHEN flow_type = 1 THEN amount ELSE 0 END) as income,
               SUM(CASE WHEN flow_type = 0 THEN amount ELSE 0 END) as expense
        FROM finance_record
        WHERE (:flowType IS NULL OR flow_type = :flowType)
        AND (:transactionTypeId IS NULL OR transaction_type_id = :transactionTypeId)
        AND (:hasImage IS NULL OR (:hasImage = 1 AND image_path IS NOT NULL) OR (:hasImage = 0 AND image_path IS NULL))
        AND (:remarkKeyword IS NULL OR remark LIKE '%' || :remarkKeyword || '%')
        AND (:startDate IS NULL OR transaction_date >= :startDate)
        AND (:endDate IS NULL OR transaction_date <= :endDate)
    """)
    suspend fun getFilteredStats(
        flowType: FlowType? = null,
        transactionTypeId: Long? = null,
        hasImage: Boolean? = null,
        remarkKeyword: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Map<String, Double?>
}
