package com.rmb.app.ui

import androidx.compose.foundation.background
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
import com.rmb.app.data.CategorySummary
import com.rmb.app.data.TransactionDao
import java.util.*

val categoryColors = mapOf(
    "餐饮" to Color(0xFFFF6B6B),
    "交通" to Color(0xFF4ECDC4),
    "购物" to Color(0xFFFFBE0B),
    "娱乐" to Color(0xFFA78BFA),
    "通讯" to Color(0xFF60A5FA),
    "住房" to Color(0xFFF472B6),
    "医疗" to Color(0xFF34D399),
    "教育" to Color(0xFFFBBF24),
    "金融" to Color(0xFF818CF8),
    "转账" to Color(0xFF94A3B8),
    "其他" to Color(0xFFCBD5E1)
)

val categoryIcons = mapOf(
    "餐饮" to "🍜",
    "交通" to "🚗",
    "购物" to "🛍️",
    "娱乐" to "🎮",
    "通讯" to "📱",
    "住房" to "🏠",
    "医疗" to "🏥",
    "教育" to "📚",
    "金融" to "💰",
    "转账" to "💸",
    "其他" to "📝"
)

@Composable
fun StatisticsScreen(dao: TransactionDao) {
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var categories by remember { mutableStateOf<List<CategorySummary>>(emptyList()) }
    var totalExpense by remember { mutableDoubleStateOf(0.0) }
    var totalIncome by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(selectedYear, selectedMonth) {
        categories = dao.getCategorySummary(selectedYear, selectedMonth, 0)
        totalExpense = dao.getMonthTotal(selectedYear, selectedMonth, 0) ?: 0.0
        totalIncome = dao.getMonthTotal(selectedYear, selectedMonth, 1) ?: 0.0
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 月份选择
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (selectedMonth == 1) { selectedYear--; selectedMonth = 12 }
                    else selectedMonth--
                }) { Icon(Icons.Default.ChevronLeft, "上月") }

                Text("${selectedYear}年${selectedMonth}月", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                IconButton(onClick = {
                    if (selectedMonth == 12) { selectedYear++; selectedMonth = 1 }
                    else selectedMonth++
                }) { Icon(Icons.Default.ChevronRight, "下月") }
            }
        }

        // 总览卡片
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("总支出", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        Text("¥%.2f".format(totalExpense), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("总收入", fontSize = 12.sp, color = Color(0xFF4CAF50))
                        Text("¥%.2f".format(totalIncome), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                }
            }
        }

        // 分类统计标题
        item {
            Text("支出分类", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        // 分类列表
        if (categories.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("暂无数据", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            items(categories) { cat ->
                CategoryItem(cat, totalExpense)
            }
        }
    }
}

@Composable
fun CategoryItem(cat: CategorySummary, total: Double) {
    val percentage = if (total > 0) (cat.total / total * 100) else 0.0
    val color = categoryColors[cat.category] ?: Color(0xFFCBD5E1)
    val icon = categoryIcons[cat.category] ?: "📝"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(cat.category, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("¥%.2f".format(cat.total), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("%.1f%%".format(percentage), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 进度条
            LinearProgressIndicator(
                progress = { (percentage / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = color,
                trackColor = color.copy(alpha = 0.15f),
            )
        }
    }
}
