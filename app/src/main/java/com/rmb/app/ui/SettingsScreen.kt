package com.rmb.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onRefresh: () -> Unit,
    onClearAll: () -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("设置", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // 重新读取短信
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRefresh
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Refresh, "刷新", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("重新读取短信", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("从手机短信中重新扫描账单", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        // 清除数据
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showClearDialog = true },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, "清除", tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("清除所有数据", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                    Text("删除所有已记录的账单数据", fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 说明
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("使用说明", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                Text("• 首次打开会自动读取手机中的账单短信", fontSize = 13.sp)
                Text("• 支持识别银行、支付宝、微信的消费/收入通知", fontSize = 13.sp)
                Text("• 自动分类：餐饮、交通、购物、娱乐等", fontSize = 13.sp)
                Text("• 数据仅存储在本地，不会上传", fontSize = 13.sp)
                Text("• 点击「重新读取」可刷新最新账单", fontSize = 13.sp)
            }
        }
    }

    // 确认清除对话框
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("确认清除") },
            text = { Text("确定要删除所有账单数据吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    onClearAll()
                    showClearDialog = false
                }) {
                    Text("确认删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
