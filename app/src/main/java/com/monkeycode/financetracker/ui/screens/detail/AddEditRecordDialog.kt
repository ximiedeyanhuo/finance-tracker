package com.monkeycode.financetracker.ui.screens.detail

import androidx.compose.foundation.layout.*
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
    
    // 自动选择第一个类型
    LaunchedEffect(flowType, types) {
        if (transactionTypeId == 0L && types.isNotEmpty()) {
            transactionTypeId = types.first().id
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = if (isEditMode) "修改记录" else "新增记录",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 金额输入
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("金额 (元) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 收/支切换
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
                
                // 交易日期
                OutlinedTextField(
                    value = transactionDate.toString(),
                    onValueChange = { },
                    label = { Text("交易日期 *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 收支类型选择
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
                
                // 备注
                OutlinedTextField(
                    value = remark,
                    onValueChange = { remark = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("最多 300 字符") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 图片上传（简化版）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("图片", style = MaterialTheme.typography.bodyMedium)
                    
                    Row {
                        if (imagePath != null) {
                            AsyncImage(
                                model = imagePath,
                                contentDescription = "已上传图片",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(end = 8.dp)
                            )
                            
                            IconButton(onClick = { imagePath = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "删除图片"
                                )
                            }
                        }
                        
                        Button(
                            onClick = { /* TODO: 实现图片选择 */ },
                            enabled = false
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("上传图片")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 保存按钮
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
