package com.rmb.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmb.app.data.Transaction
import com.rmb.app.data.TransactionDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(dao: TransactionDao) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var availableYears by remember { mutableStateOf<List<Int>>(emptyList()) }
    var filterType by remember { mutableIntStateOf(-1) } // -1=全部, 0=支出, 1=收入
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(selectedYear, selectedMonth, filterType) {
        transactions = when (filterType) {
            -1 -> dao.getByMonth(selectedYear, selectedMonth)
            0 -> dao.getByYearMonthType(selectedYear, selectedMonth, 0)
            1 -> dao.getByYearMonthType(selectedYear, selectedMonth, 1)
            else -> dao.getByMonth(selectedYear, selectedMonth)
        }
        availableYears = dao.getAvailableYears().ifEmpty { listOf(selectedYear) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 月份选择器
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedMonth == 1) { selectedYear--; selectedMonth = 12 }
                else selectedMonth--
            }) {
                Icon(Icons.Default.ChevronLeft, "上月")
            }

            Text(
                "${selectedYear}年${selectedMonth}月",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                if (selectedMonth == 12) { selectedYear++; selectedMonth = 1 }
                else selectedMonth++
            }) {
                Icon(Icons.Default.ChevronRight, "下月")
            }
        }

        // 筛选按钮
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == -1,
                onClick = { filterType = -1 },
                label = { Text("全部") }
            )
            FilterChip(
                selected = filterType == 0,
                onClick = { filterType = 0 },
                label = { Text("支出") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer
                )
            )
            FilterChip(
                selected = filterType == 1,
                onClick = { filterType = 1 },
                label = { Text("收入") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE8F5E9)
                )
            )
        }

        // 汇总
        val totalExpense = transactions.filter { it.type == 0 }.sumOf { it.amount }
        val totalIncome = transactions.filter { it.type == 1 }.sumOf { it.amount }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("支出", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                Text("¥%.2f".format(totalExpense), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("收入", fontSize = 12.sp, color = Color(0xFF4CAF50))
                Text("¥%.2f".format(totalIncome), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("结余", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Text("¥%.2f".format(totalIncome - totalExpense), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        HorizontalDivider()

        // 交易列表
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无账单数据", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions) { tx ->
                    TransactionItem(tx, dateFormat)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction, dateFormat: SimpleDateFormat) {
    val isExpense = tx.type == 0
    val amountColor = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
    val amountPrefix = if (isExpense) "-" else "+"
    val icon = when (tx.category) {
        "餐饮" -> "🍜"
        "交通" -> "🚗"
        "购物" -> "🛍️"
        "娱乐" -> "🎮"
        "通讯" -> "📱"
        "住房" -> "🏠"
        "医疗" -> "🏥"
        "教育" -> "📚"
        "金融" -> "💰"
        "转账" -> "💸"
        else -> "📝"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isExpense) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9),
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 详情
        Column(modifier = Modifier.weight(1f)) {
            Text(
                tx.category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                tx.merchant.ifEmpty { tx.bankName.ifEmpty { tx.source } },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // 金额和时间
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$amountPrefix¥%.2f".format(tx.amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
            Text(
                dateFormat.format(Date(tx.timestamp)),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
