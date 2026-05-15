package com.monkeycode.financetracker.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateSelected(date)
                    }
                    onDismiss()
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DateRangePickerComponent(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var showStartPicker by remember { mutableStateOf(true) }
    
    if (showStartPicker) {
        DatePickerComponent(
            initialDate = startDate,
            onDateSelected = onStartDateSelected,
            onDismiss = {
                showStartPicker = false
            }
        )
    } else {
        DatePickerComponent(
            initialDate = endDate,
            onDateSelected = { date ->
                onEndDateSelected(date)
                onDismiss()
            },
            onDismiss = {
                showStartPicker = true
            }
        )
    }
}

fun LocalDate.formatDisplay(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
