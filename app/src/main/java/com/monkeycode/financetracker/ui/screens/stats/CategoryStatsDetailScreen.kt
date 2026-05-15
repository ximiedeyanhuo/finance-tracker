package com.monkeycode.financetracker.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monkeycode.financetracker.domain.model.CategoryStats
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.ui.theme.*
import com.monkeycode.financetracker.ui.viewmodel.CategoryStatsViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryStatsDetailScreen(
    navController: NavController,
    flowTypeValue: Int,
    startDateStr: String,
    endDateStr: String,
    viewModel: CategoryStatsViewModel = hiltViewModel()
) {
    val flowType = FlowType.fromValue(flowTypeValue)
    val startDate = LocalDate.parse(startDateStr)
    val endDate = LocalDate.parse(endDateStr)
    val categoryStats by viewModel.categoryStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(flowTypeValue, startDateStr, endDateStr) {
        viewModel.loadStats(startDate, endDate, flowType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (flowType == FlowType.EXPENSE) "支出分类统计"
                        else "收入分类统计"
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 统计摘要
            CategoryStatsSummary(
                categoryStats = categoryStats,
                flowType = flowType,
                modifier = Modifier.fillMaxWidth()
            )

            // 日期范围显示
            Text(
                text = "${startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} ~ ${endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}",
                style = MaterialTheme.typography.bodySmall,
                color = TextHint,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 数据列表
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (categoryStats.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无数据", color = TextHint)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = categoryStats,
                        key = { _, stat -> stat.transactionTypeId }
                    ) { index, stat ->
                        CategoryStatsItem(
                            stat = stat,
                            index = index,
                            flowType = flowType
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryStatsSummary(
    categoryStats: List<CategoryStats>,
    flowType: FlowType,
    modifier: Modifier = Modifier
) {
    val totalAmount = categoryStats.fold(BigDecimal.ZERO) { acc, stat -> acc + stat.totalAmount }
    val totalCount = categoryStats.sumOf { it.count }
    val labelColor = if (flowType == FlowType.EXPENSE) Expense else Income
    val label = if (flowType == FlowType.EXPENSE) "支出总额" else "收入总额"

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
            Text(label, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCategoryAmount(totalAmount),
                color = labelColor,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("总笔数", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${totalCount}笔",
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("类型数", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${categoryStats.size}类",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryStatsItem(
    stat: CategoryStats,
    index: Int,
    flowType: FlowType
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    val amountColor = if (flowType == FlowType.EXPENSE) Expense else Income

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stat.transactionTypeName,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${stat.count}笔",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCategoryAmount(stat.totalAmount),
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
                val totalAmount = stat.totalAmount.toDouble()
                if (totalAmount > 0) {
                    Text(
                        text = "占比 ${String.format("%.1f", totalAmount / (stat.totalAmount.toDouble() + 0.01) * 100)}%",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun formatCategoryAmount(amount: BigDecimal): String {
    val str = amount.stripTrailingZeros().toPlainString()
    return if (amount.scale() <= 0) amount.toPlainString() else str
}