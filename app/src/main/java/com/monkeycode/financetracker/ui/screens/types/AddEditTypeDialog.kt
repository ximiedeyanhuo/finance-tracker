package com.monkeycode.financetracker.ui.screens.types

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.ui.theme.TextSecondary

@Composable
fun AddEditTypeDialog(
    type: TransactionType? = null,
    onSave: (TransactionType) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditMode = type != null
    
    var flowType by remember { mutableStateOf(type?.flowType ?: FlowType.EXPENSE) }
    var name by remember { mutableStateOf(type?.name ?: "") }
    var sortOrder by remember { mutableStateOf(type?.sortOrder?.toString() ?: "0") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isEditMode) "修改类型" else "新增类型",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 收/支切换
                Row {
                    FilterChip(
                        selected = flowType == FlowType.EXPENSE,
                        onClick = { flowType = FlowType.EXPENSE },
                        label = { Text("支出") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = flowType == FlowType.INCOME,
                        onClick = { flowType = FlowType.INCOME },
                        label = { Text("收入") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 名称输入
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("名称 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("最多 10 字符") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 排序序号
                OutlinedTextField(
                    value = sortOrder,
                    onValueChange = { sortOrder = it },
                    label = { Text("排序序号") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = { Text("越小越靠前") }
                )
                
                if (isEditMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "修改收支类型将会同步修改业务数据收支类型",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 按钮
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
                            if (name.isNotBlank()) {
                                val newType = TransactionType(
                                    id = type?.id ?: 0,
                                    name = name,
                                    flowType = flowType,
                                    sortOrder = sortOrder.toIntOrNull() ?: 0
                                )
                                onSave(newType)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (isEditMode) "保存" else "新增")
                    }
                }
            }
        }
    }
}
