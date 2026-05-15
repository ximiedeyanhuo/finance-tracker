package com.monkeycode.financetracker.ui.screens.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.monkeycode.financetracker.domain.model.FlowType
import com.monkeycode.financetracker.domain.model.TransactionType
import com.monkeycode.financetracker.ui.theme.*
import com.monkeycode.financetracker.ui.viewmodel.TransactionTypeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTypeScreen(
    navController: NavController,
    viewModel: TransactionTypeViewModel = hiltViewModel()
) {
    val allTypes by viewModel.allTypes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收支类型") }
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
            // 筛选区
            TypeFilter(
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
            } else if (allTypes.isEmpty()) {
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
                        items = allTypes,
                        key = { _, type -> type.id }
                    ) { index, type ->
                        TransactionTypeItem(
                            type = type,
                            index = index,
                            onEdit = { selectedType = type },
                            onDelete = { 
                                viewModel.deleteType(type) {
                                    // 刷新成功
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddEditTypeDialog(
            type = null,
            onSave = { newType ->
                viewModel.addType(newType) {
                    showAddDialog = false
                }
            },
            onDismiss = { showAddDialog = false }
        )
    }
    
    if (selectedType != null) {
        AddEditTypeDialog(
            type = selectedType!!,
            onSave = { updatedType ->
                viewModel.updateType(updatedType) {
                    selectedType = null
                }
            },
            onDismiss = { selectedType = null }
        )
    }
}

@Composable
fun TypeFilter(
    modifier: Modifier = Modifier
) {
    var selectedFlowType by remember { mutableStateOf<FlowType?>(null) }
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = selectedFlowType == null,
                    onClick = { selectedFlowType = null },
                    label = { Text("全部") }
                )
                FilterChip(
                    selected = selectedFlowType == FlowType.EXPENSE,
                    onClick = { selectedFlowType = FlowType.EXPENSE },
                    label = { Text("支出") }
                )
                FilterChip(
                    selected = selectedFlowType == FlowType.INCOME,
                    onClick = { selectedFlowType = FlowType.INCOME },
                    label = { Text("收入") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* TODO: 重置 */ }) {
                    Text("重置")
                }
                Button(onClick = { /* TODO: 查询 */ }) {
                    Text("查询")
                }
            }
        }
    }
}
