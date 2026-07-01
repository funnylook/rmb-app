package com.rmb.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.rmb.app.data.TransactionDao
import kotlinx.coroutines.launch
import java.util.*

data class MonthData(
    val month: Int,
    val expense: Double,
    val income: Double
)

@Composable
fun YearlyScreen(dao: TransactionDao) {
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var monthDataList by remember { mutableStateOf<List<MonthData>>(emptyList()) }
    var yearTotalExpense by remember { mutableDoubleStateOf(0.0) }
    var yearTotalIncome by remember { mutableDoubleStateOf(0.0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedYear) {
        val list = mutableListOf<MonthData>()
        for (m in 1..12) {
            val expense = dao.getMonthTotal(selectedYear, m, 0) ?: 0.0
            val income = dao.getMonthTotal(selectedYear, m, 1) ?: 0.0
            if (expense > 0 || income > 0) {
                list.add(MonthData(m, expense, income))
            }
        }
        monthDataList = list
        yearTotalExpense = dao.getYearTotal(selectedYear, 0) ?: 0.0
        yearTotalIncome = dao.getYearTotal(selectedYear, 1) ?: 0.0
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 年份选择
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedYear-- }) {
                    Icon(Icons.Default.ChevronLeft, "上一年")
                }
                Text("${selectedYear}年", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { selectedYear++ }) {
                    Icon(Icons.Default.ChevronRight, "下一年")
                }
            }
        }

        // 年度总览
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("年度总览", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("总支出", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            Text("¥%.0f".format(yearTotalExpense), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("总收入", fontSize = 12.sp, color = Color(0xFF4CAF50))
                            Text("¥%.0f".format(yearTotalIncome), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("结余", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Text("¥%.0f".format(yearTotalIncome - yearTotalExpense), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 月均
                    val activeMonths = monthDataList.size.coerceAtLeast(1)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("月均支出: ¥%.0f".format(yearTotalExpense / activeMonths), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        Text("月均收入: ¥%.0f".format(yearTotalIncome / activeMonths), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        // 月度列表标题
        item {
            Text("月度明细", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        // 月度列表
        if (monthDataList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("暂无数据", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            items(monthDataList) { md ->
                MonthItem(md)
            }
        }
    }
}

@Composable
fun MonthItem(md: MonthData) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${md.month}月", fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(40.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("支出", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text("¥%.0f".format(md.expense), fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("收入", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text("¥%.0f".format(md.income), fontSize = 14.sp, color = Color(0xFF4CAF50))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("结余", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text("¥%.0f".format(md.income - md.expense), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
