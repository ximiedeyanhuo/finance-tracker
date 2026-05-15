package com.monkeycode.financetracker.ui.screens.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.ui.components.ConfirmationDialog
import com.monkeycode.financetracker.ui.theme.*

@Composable
fun TransactionTypeItem(
    type: TransactionType,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    var showDeleteDialog by remember { mutableStateOf(false) }
    
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (index + 1).toString(),
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White)
                            .padding(4.dp),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = type.name,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row {
                    Badge(
                        containerColor = if (type.flowType.value == 0) Expense else Income
                    ) {
                        Text(
                            text = if (type.flowType.value == 0) "支出" else "收入",
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "顺序：${type.sortOrder}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextHint
                    )
                }
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "修改",
                        tint = Color.Gray
                    )
                }
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = Color.Red
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "确认删除",
            message = "删除后不可恢复，是否继续？",
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
