package com.monkeycode.financetracker.ui.screens.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.ui.components.ImagePicker
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditRecordDialog(
    record: FinanceRecord? = null,
    transactionTypesExpense: List<TransactionType>,
    transactionTypesIncome: List<TransactionType>,
    onSave: (FinanceRecord) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditMode = record != null
    
    var amountStr by remember { mutableStateOf(record?.amount?.stripTrailingZeros()?.toPlainString() ?: "") }
    var transactionDate by remember { mutableStateOf(record?.transactionDate ?: LocalDate.now()) }
    var flowType by remember { mutableStateOf(record?.flowType ?: FlowType.EXPENSE) }
    var transactionTypeId by remember { mutableStateOf(record?.transactionTypeId ?: 0L) }
    var remark by remember { mutableStateOf(record?.remark ?: "") }
    var imagePath by remember { mutableStateOf(record?.imagePath) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val types = if (flowType == FlowType.EXPENSE) {
        transactionTypesExpense
    } else {
        transactionTypesIncome
    }
    
    LaunchedEffect(flowType, types) {
        if (transactionTypeId == 0L && types.isNotEmpty()) {
            transactionTypeId = types.first().id
        }
    }
    
    if (showDatePicker) {
        com.monkeycode.financetracker.ui.components.DatePickerComponent(
            initialDate = transactionDate,
            onDateSelected = { transactionDate = it },
            onDismiss = { showDatePicker = false }
        )
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isEditMode) "修改记录" else "新增记录",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("金额 (元) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row {
                    FilterChip(
                        selected = flowType == FlowType.EXPENSE,
                        onClick = {
                            flowType = FlowType.EXPENSE
                            if (transactionTypesExpense.isNotEmpty()) {
                                transactionTypeId = transactionTypesExpense.first().id
                            }
                        },
                        label = { Text("支出") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = flowType == FlowType.INCOME,
                        onClick = {
                            flowType = FlowType.INCOME
                            if (transactionTypesIncome.isNotEmpty()) {
                                transactionTypeId = transactionTypesIncome.first().id
                            }
                        },
                        label = { Text("收入") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onValueChange = { },
                    label = { Text("交易日期 *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "选择日期"
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (types.isEmpty()) {
                    Text(
                        text = "请先添加收支类型！",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else {
                    Text("收支类型 *", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        types.forEach { type ->
                            FilterChip(
                                selected = type.id == transactionTypeId,
                                onClick = { transactionTypeId = type.id },
                                label = { Text(type.name) },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = remark,
                    onValueChange = { remark = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("最多 300 字符") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ImagePicker(
                    selectedImagePath = imagePath,
                    onImageSelected = { path -> imagePath = path },
                    onImageRemoved = { imagePath = null }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (amountStr.isNotBlank() && transactionTypeId != 0L) {
                                val newRecord = FinanceRecord(
                                    id = record?.id ?: 0,
                                    amount = BigDecimal(amountStr),
                                    flowType = flowType,
                                    transactionTypeId = transactionTypeId,
                                    transactionDate = transactionDate,
                                    remark = remark.ifBlank { null },
                                    imagePath = imagePath,
                                    updateTime = java.time.LocalDateTime.now()
                                )
                                onSave(newRecord)
                            }
                        },
                        enabled = amountStr.isNotBlank() && transactionTypeId != 0L
                    ) {
                        Text(if (isEditMode) "保存" else "新增")
                    }
                }
            }
        }
    }
}
