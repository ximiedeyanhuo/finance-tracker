package com.monkeycode.financetracker.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monkeycode.financetracker.domain.model.FinanceRecord
import com.monkeycode.financetracker.ui.components.AmountText
import com.monkeycode.financetracker.ui.components.ConfirmationDialog
import com.monkeycode.financetracker.ui.theme.RowEven
import com.monkeycode.financetracker.ui.theme.RowOdd
import com.monkeycode.financetracker.ui.theme.TextHint
import com.monkeycode.financetracker.ui.theme.TextPrimary
import com.monkeycode.financetracker.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@Composable
fun FinanceRecordItem(
    record: FinanceRecord,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetail: () -> Unit
) {
    val backgroundColor = if (index % 2 == 0) RowEven else RowOdd
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onViewDetail() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：序号 + 金额 + 类型
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 序号
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    // 金额
                    AmountText(
                        amount = record.amount,
                        isExpense = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 收支类型
                    Text(
                        text = record.transactionTypeName ?: "未分类",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // 日期和图片标识
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record.transactionDate.format(DateTimeFormatter.ofPattern("MM-dd")),
                            color = TextHint,
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        if (record.imagePath != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "有图片",
                                modifier = Modifier.size(16.dp),
                                tint = TextHint
                            )
                        }
                    }
                    
                    // 备注
                    if (!record.remark.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.remark!!,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // 右侧：操作按钮
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "修改",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(onClick = { /* TODO: 复制功能 */ }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "复制",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
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
