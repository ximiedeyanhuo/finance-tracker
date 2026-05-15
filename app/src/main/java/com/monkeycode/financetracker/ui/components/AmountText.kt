package com.monkeycode.financetracker.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import com.monkeycode.financetracker.ui.theme.Expense
import com.monkeycode.financetracker.ui.theme.Income
import java.math.BigDecimal

@Composable
fun AmountText(
    amount: BigDecimal,
    isExpense: Boolean,
    modifier: Modifier = Modifier,
    bold: Boolean = true
) {
    val color = if (isExpense) Expense else Income
    val displayAmount = formatAmount(amount)
    
    Text(
        text = displayAmount,
        modifier = modifier,
        color = color,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
    )
}

fun formatAmount(amount: BigDecimal): String {
    val str = amount.stripTrailingZeros().toPlainString()
    return if (amount.scale() <= 0) {
        amount.toPlainString()
    } else {
        str
    }
}
