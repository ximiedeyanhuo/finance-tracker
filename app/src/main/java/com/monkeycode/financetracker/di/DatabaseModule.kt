package com.monkeycode.financetracker.di

import android.content.Context
import androidx.room.Room
import com.monkeycode.financetracker.data.local.AppDatabase
import com.monkeycode.financetracker.data.local.DatabaseInitializerCallback
import com.monkeycode.financetracker.data.local.FinanceRecordDao
import com.monkeycode.financetracker.data.local.TransactionTypeDao
import com.monkeycode.financetracker.data.repository.FinanceRepository
import com.monkeycode.financetracker.data.repository.TransactionTypeRepository
import com.monkeycode.financetracker.domain.service.ExportService
import com.monkeycode.financetracker.domain.service.ImageService
import com.monkeycode.financetracker.domain.service.StatsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).addCallback(DatabaseInitializerCallback())
         .build()
    }

    @Provides
    @Singleton
    fun provideFinanceRecordDao(database: AppDatabase): FinanceRecordDao {
        return database.financeRecordDao()
    }

    @Provides
    @Singleton
    fun provideTransactionTypeDao(database: AppDatabase): TransactionTypeDao {
        return database.transactionTypeDao()
    }

    @Provides
    @Singleton
    fun provideFinanceRepository(dao: FinanceRecordDao): FinanceRepository {
        return FinanceRepository(dao)
    }

    @Provides
    @Singleton
    fun provideTransactionTypeRepository(dao: TransactionTypeDao): TransactionTypeRepository {
        return TransactionTypeRepository(dao)
    }

    @Provides
    @Singleton
    fun provideImageService(@ApplicationContext context: Context): ImageService {
        return ImageService(context)
    }

    @Provides
    @Singleton
    fun provideExportService(@ApplicationContext context: Context): ExportService {
        return ExportService(context)
    }

    @Provides
    @Singleton
    fun provideStatsService(repository: FinanceRepository): StatsService {
        return StatsService(repository)
    }
}
