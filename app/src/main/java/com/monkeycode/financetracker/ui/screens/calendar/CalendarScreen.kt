package com.monkeycode.financetracker.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monkeycode.financetracker.ui.components.AmountText
import com.monkeycode.financetracker.ui.theme.*
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: com.monkeycode.financetracker.ui.viewmodel.FinanceViewModel = hiltViewModel()
) {
    val currentDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val records by viewModel.records.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 月份导航栏
            MonthNavigation(
                currentMonth = currentDate.value,
                onPreviousMonth = {
                    currentDate.value = currentDate.value.minusMonths(1)
                },
                onNextMonth = {
                    currentDate.value = currentDate.value.plusMonths(1)
                }
            )
            
            // 月度统计摘要
            val monthRecords = records.filter { 
                it.transactionDate.year == currentDate.value.year && 
                it.transactionDate.month == currentDate.value.month 
            }
            MonthStatsSummary(
                records = monthRecords,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 日历网格
            CalendarGrid(
                currentMonth = currentDate.value,
                selectedDate = selectedDate.value,
                records = records,
                onDateSelected = { selectedDate.value = it }
            )
            
            // 当日明细面板
            DayDetailPanel(
                selectedDate = selectedDate.value,
                records = records.filter { it.transactionDate == selectedDate.value }
            )
        }
    }
}

@Composable
fun MonthNavigation(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "上月"
            )
        }
        
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy 年 MM 月")),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "下月"
            )
        }
    }
}

@Composable
fun MonthStatsSummary(
    records: List<com.monkeycode.financetracker.domain.model.FinanceRecord>,
    modifier: Modifier = Modifier
) {
    val income = records.filter { it.flowType.value == 1 }
        .sumOf { it.amount.toDouble() }.toBigDecimal()
    val expense = records.filter { it.flowType.value == 0 }
        .sumOf { it.amount.toDouble() }.toBigDecimal()
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
            Text("月收入", color = TextSecondary)
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
            Text("月支出", color = TextSecondary)
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
fun CalendarGrid(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    records: List<com.monkeycode.financetracker.domain.model.FinanceRecord>,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(currentMonth)
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    val startDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endDate = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        var current = startDate
        while (!current.isAfter(endDate)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    val day = current.plusDays(i.toLong())
                    val dayRecords = records.filter { it.transactionDate == day }
                    val dayIncome = dayRecords.filter { it.flowType.value == 1 }
                        .sumOf { it.amount.toDouble() }.toBigDecimal()
                    val dayExpense = dayRecords.filter { it.flowType.value == 0 }
                        .sumOf { it.amount.toDouble() }.toBigDecimal()
                    
                    CalendarCell(
                        date = day,
                        currentMonth = yearMonth,
                        isSelected = day == selectedDate,
                        isToday = day == LocalDate.now(),
                        income = dayIncome,
                        expense = dayExpense,
                        onClick = { onDateSelected(day) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            current = current.plusWeeks(1)
        }
    }
}

@Composable
fun CalendarCell(
    date: LocalDate,
    currentMonth: YearMonth,
    isSelected: Boolean,
    isToday: Boolean,
    income: BigDecimal,
    expense: BigDecimal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInMonth = date.month == currentMonth.month
    val backgroundColor = when {
        isSelected -> RowToday
        isToday -> Color(0xFFFFEB3B).copy(alpha = 0.3f)
        !isInMonth -> RowOutsideMonth
        else -> Color.Transparent
    }
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text(
                text = date.dayOfMonth.toString(),
                modifier = Modifier.padding(4.dp),
                color = if (isInMonth) TextPrimary else TextHint
            )
            
            if (isInMonth) {
                if (income > BigDecimal.ZERO) {
                    Text(
                        text = formatAmount(income),
                        style = MaterialTheme.typography.bodySmall,
                        color = Income,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                if (expense > BigDecimal.ZERO) {
                    Text(
                        text = formatAmount(expense),
                        style = MaterialTheme.typography.bodySmall,
                        color = Expense,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayDetailPanel(
    selectedDate: LocalDate,
    records: List<com.monkeycode.financetracker.domain.model.FinanceRecord>,
    modifier: Modifier = Modifier
) {
    val dayIncome = records.filter { it.flowType.value == 1 }
        .sumOf { it.amount.toDouble() }.toBigDecimal()
    val dayExpense = records.filter { it.flowType.value == 0 }
        .sumOf { it.amount.toDouble() }.toBigDecimal()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("当日收入：${formatAmount(dayIncome)}", color = Income)
                Text("当日支出：${formatAmount(dayExpense)}", color = Expense)
            }
            
            if (records.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "当日暂无记录",
                    color = TextHint,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "当日明细 (${records.size}笔)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                records.forEachIndexed { index, record ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = record.transactionTypeName ?: "未分类",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = (index + 1).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextHint
                            )
                        }
                        AmountText(
                            amount = record.amount,
                            isExpense = record.flowType.value == 0
                        )
                    }
                }
            }
        }
    }
}

private fun formatAmount(amount: BigDecimal): String {
    val str = amount.stripTrailingZeros().toPlainString()
    return if (amount.scale() <= 0) amount.toPlainString() else str
}
