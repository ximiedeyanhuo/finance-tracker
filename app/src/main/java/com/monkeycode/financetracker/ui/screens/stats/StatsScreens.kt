package com.monkeycode.financetracker.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monkeycode.financetracker.domain.model.DailyStats
import com.monkeycode.financetracker.domain.model.MonthlyStats
import com.monkeycode.financetracker.domain.model.WeeklyStats
import com.monkeycode.financetracker.domain.model.YearlyStats
import com.monkeycode.financetracker.ui.components.AmountText
import com.monkeycode.financetracker.ui.theme.*
import com.monkeycode.financetracker.ui.viewmodel.DailyStatsViewModel
import com.monkeycode.financetracker.ui.viewmodel.MonthlyStatsViewModel
import com.monkeycode.financetracker.ui.viewmodel.WeeklyStatsViewModel
import com.monkeycode.financetracker.ui.viewmodel.YearlyStatsViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStatsScreen(
    navController: NavController,
    viewModel: DailyStatsViewModel = hiltViewModel()
) {
    val dailyStats by viewModel.dailyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    
    LaunchedEffect(Unit) {
        viewModel.loadStats(startDate, endDate)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日账") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StatsSummaryRow(dailyStats = dailyStats)
            
            DateRangeFilter(
                startDate = startDate,
                endDate = endDate,
                onQuery = { start, end ->
                    startDate = start
                    endDate = end
                    viewModel.loadStats(start, end)
                }
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (dailyStats.isEmpty()) {
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
                        items = dailyStats,
                        key = { _, stat -> stat.date }
                    ) { index, stat ->
                        DailyStatsItem(stat = stat, index = index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyStatsScreen(
    navController: NavController,
    viewModel: WeeklyStatsViewModel = hiltViewModel()
) {
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var referenceDate by remember { mutableStateOf(LocalDate.now()) }
    
    LaunchedEffect(Unit) {
        viewModel.loadStats(referenceDate)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("周账") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StatsSummaryRow(weeklyStats = weeklyStats)
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (weeklyStats.isEmpty()) {
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
                        items = weeklyStats,
                        key = { _, stat -> stat.startDate }
                    ) { index, stat ->
                        WeeklyStatsItem(stat = stat, index = index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyStatsScreen(
    navController: NavController,
    viewModel: MonthlyStatsViewModel = hiltViewModel()
) {
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val currentYear = YearMonth.now().year
    var startMonth by remember { mutableStateOf(YearMonth.of(currentYear, 1)) }
    var endMonth by remember { mutableStateOf(YearMonth.now()) }
    
    LaunchedEffect(Unit) {
        viewModel.loadStats(startMonth, endMonth)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("月账") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StatsSummaryRow(monthlyStats = monthlyStats)
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (monthlyStats.isEmpty()) {
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
                        items = monthlyStats,
                        key = { _, stat -> stat.yearMonth }
                    ) { index, stat ->
                        MonthlyStatsItem(stat = stat, index = index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlyStatsScreen(
    navController: NavController,
    viewModel: YearlyStatsViewModel = hiltViewModel()
) {
    val yearlyStats by viewModel.yearlyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val currentYear = LocalDate.now().year
    var startYear by remember { mutableStateOf(currentYear - 5) }
    var endYear by remember { mutableStateOf(currentYear) }
    
    LaunchedEffect(Unit) {
        viewModel.loadStats(startYear, endYear)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("年账") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StatsSummaryRow(yearlyStats = yearlyStats)
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (yearlyStats.isEmpty()) {
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
                        items = yearlyStats,
                        key = { _, stat -> stat.year }
                    ) { index, stat ->
                        YearlyStatsItem(stat = stat, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSummaryRow(
    dailyStats: List<DailyStats>? = null,
    weeklyStats: List<WeeklyStats>? = null,
    monthlyStats: List<MonthlyStats>? = null,
    yearlyStats: List<YearlyStats>? = null,
    modifier: Modifier = Modifier
) {
    val (income, expense) = when {
        dailyStats != null -> {
            val i = dailyStats.sumOf { it.income.toDouble() }.toBigDecimal()
            val e = dailyStats.sumOf { it.expense.toDouble() }.toBigDecimal()
            Pair(i, e)
        }
        weeklyStats != null -> {
            val i = weeklyStats.sumOf { it.income.toDouble() }.toBigDecimal()
            val e = weeklyStats.sumOf { it.expense.toDouble() }.toBigDecimal()
            Pair(i, e)
        }
        monthlyStats != null -> {
            val i = monthlyStats.sumOf { it.income.toDouble() }.toBigDecimal()
            val e = monthlyStats.sumOf { it.expense.toDouble() }.toBigDecimal()
            Pair(i, e)
        }
        yearlyStats != null -> {
            val i = yearlyStats.sumOf { it.income.toDouble() }.toBigDecimal()
            val e = yearlyStats.sumOf { it.expense.toDouble() }.toBigDecimal()
            Pair(i, e)
        }
        else -> Pair(BigDecimal.ZERO, BigDecimal.ZERO)
    }
    
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
            Text(
                text = formatAmount(income),
                color = Income,
                fontWeight = FontWeight.Bold
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("支出总额", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatAmount(expense),
                color = Expense,
                fontWeight = FontWeight.Bold
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("结余", color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatAmount(balance),
                color = if (balance >= BigDecimal.ZERO) BalancePositive else BalanceNegative,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyStatsItem(
    stat: DailyStats,
    index: Int
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    
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
                    text = stat.date.format(DateTimeFormatter.ofPattern("MM-dd")),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("收入：${formatAmount(stat.income)}", color = Income)
                Text("支出：${formatAmount(stat.expense)}", color = Expense)
                Text("结余：${formatAmount(stat.balance)}")
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("收入${stat.incomeCount}笔")
                Text("支出${stat.expenseCount}笔")
            }
        }
    }
}

@Composable
fun WeeklyStatsItem(
    stat: WeeklyStats,
    index: Int
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    
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
                val weekLabel = "${stat.startDate.format(DateTimeFormatter.ofPattern("MM-dd"))} ~ ${stat.endDate.format(DateTimeFormatter.ofPattern("MM-dd"))}"
                Text(
                    text = weekLabel,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("收入：${formatAmount(stat.income)}", color = Income)
                Text("支出：${formatAmount(stat.expense)}", color = Expense)
                Text("结余：${formatAmount(stat.balance)}")
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("收入${stat.incomeCount}笔")
                Text("支出${stat.expenseCount}笔")
            }
        }
    }
}

@Composable
fun MonthlyStatsItem(
    stat: MonthlyStats,
    index: Int
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    
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
                    text = stat.yearMonth,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("收入：${formatAmount(stat.income)}", color = Income)
                Text("支出：${formatAmount(stat.expense)}", color = Expense)
                Text("结余：${formatAmount(stat.balance)}")
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("收入${stat.incomeCount}笔")
                Text("支出${stat.expenseCount}笔")
            }
        }
    }
}

@Composable
fun YearlyStatsItem(
    stat: YearlyStats,
    index: Int
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    
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
                    text = stat.year,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("收入：${formatAmount(stat.income)}", color = Income)
                Text("支出：${formatAmount(stat.expense)}", color = Expense)
                Text("结余：${formatAmount(stat.balance)}")
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("收入${stat.incomeCount}笔")
                Text("支出${stat.expenseCount}笔")
            }
        }
    }
}

@Composable
fun DateRangeFilter(
    startDate: LocalDate,
    endDate: LocalDate,
    onQuery: (LocalDate, LocalDate) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                onValueChange = { },
                label = { Text("开始日期") },
                modifier = Modifier.weight(1f),
                readOnly = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedTextField(
                value = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                onValueChange = { },
                label = { Text("结束日期") },
                modifier = Modifier.weight(1f),
                readOnly = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(onClick = { onQuery(startDate, endDate) }) {
                Text("查询")
            }
        }
    }
}

private fun formatAmount(amount: BigDecimal): String {
    val str = amount.stripTrailingZeros().toPlainString()
    return if (amount.scale() <= 0) amount.toPlainString() else str
}
