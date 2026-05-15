package com.monkeycode.financetracker.data.local

import androidx.room.*
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.data.model.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionTypeDao {

    @Query("SELECT * FROM transaction_type WHERE flow_type = :flowType ORDER BY sort_order, id")
    fun getByFlowType(flowType: FlowType): Flow<List<TransactionTypeEntity>>

    @Query("SELECT * FROM transaction_type ORDER BY flow_type, sort_order, id")
    fun getAll(): Flow<List<TransactionTypeEntity>>

    @Query("SELECT * FROM transaction_type WHERE id = :id")
    suspend fun getById(id: Long): TransactionTypeEntity?

    @Query("SELECT COUNT(*) FROM finance_record WHERE transaction_type_id = :id")
    suspend fun getUsageCount(id: Long): Int

    @Query("SELECT COUNT(*) FROM transaction_type")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: TransactionTypeEntity): Long

    @Update
    suspend fun update(type: TransactionTypeEntity)

    @Delete
    suspend fun delete(type: TransactionTypeEntity)

    @Query("""
        SELECT COUNT(*) FROM transaction_type 
        WHERE flow_type = :flowType AND name = :name AND id != :excludeId
    """)
    suspend fun checkDuplicateName(flowType: FlowType, name: String, excludeId: Long = 0): Int

    @Query("DELETE FROM transaction_type")
    suspend fun deleteAll()
}
