package com.monkeycode.financetracker.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.ui.components.AmountText
import com.monkeycode.financetracker.ui.navigation.Screen
import com.monkeycode.financetracker.ui.theme.*
import com.monkeycode.financetracker.ui.utils.ShareUtils
import com.monkeycode.financetracker.ui.viewmodel.FinanceViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceDetailScreen(
    navController: NavController,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val totalStats by viewModel.totalStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<FinanceRecord?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("财务明细") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "筛选"
                        )
                    }
                    IconButton(onClick = {
                        if (records.isNotEmpty()) {
                            viewModel.exportRecords(records) { uri ->
                                val shareIntent = ShareUtils.createCsvShareIntent(
                                    context,
                                    uri,
                                    "账务明细.csv"
                                )
                                context.startActivity(shareIntent)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "导出"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新增",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 顶部统计栏
            StatsBar(
                income = totalStats.first,
                expense = totalStats.second,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 数据列表
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无数据", color = TextHint)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = records.size,
                        key = { index -> records[index].id }
                    ) { index ->
                        val record = records[index]
                        FinanceRecordItem(
                            record = record,
                            index = index,
                            onEdit = { selectedRecord = record },
                            onDelete = { 
                                viewModel.deleteRecord(record) {
                                    // 刷新成功
                                }
                            },
                            onViewDetail = { selectedRecord = record }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddEditRecordDialog(
            transactionTypesExpense = viewModel.transactionTypesExpense.collectAsState().value,
            transactionTypesIncome = viewModel.transactionTypesIncome.collectAsState().value,
            onSave = { newRecord ->
                viewModel.addRecord(newRecord) {
                    showAddDialog = false
                }
            },
            onDismiss = { showAddDialog = false }
        )
    }
    
    if (selectedRecord != null) {
        AddEditRecordDialog(
            record = selectedRecord,
            transactionTypesExpense = viewModel.transactionTypesExpense.collectAsState().value,
            transactionTypesIncome = viewModel.transactionTypesIncome.collectAsState().value,
            onSave = { updatedRecord ->
                viewModel.updateRecord(updatedRecord) {
                    selectedRecord = null
                }
            },
            onDismiss = { selectedRecord = null }
        )
    }
}

@Composable
fun StatsBar(
    income: BigDecimal,
    expense: BigDecimal,
    modifier: Modifier = Modifier
) {
    val balance = income - expense
    
    Row(
        modifier = modifier
            .background(Surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("收入总额", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            AmountText(amount = income, isExpense = false)
        }
        
        HorizontalDivider(
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("支出总额", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            AmountText(amount = expense, isExpense = true)
        }
        
        HorizontalDivider(
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("结余", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = balance.toPlainString(),
                fontWeight = FontWeight.Bold,
                color = if (balance >= BigDecimal.ZERO) BalancePositive else BalanceNegative
            )
        }
    }
}
