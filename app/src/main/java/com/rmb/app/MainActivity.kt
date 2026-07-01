package com.rmb.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.rmb.app.data.AppDatabase
import com.rmb.app.ui.*
import com.rmb.app.ui.theme.RmbTheme
import com.rmb.app.util.SmsReader
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadSmsData()
        } else {
            Toast.makeText(this, "需要短信权限才能读取账单", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = (application as RmbApp).database

        setContent {
            RmbTheme {
                MainApp(
                    onRequestSms = { requestSmsPermission() },
                    db = db
                )
            }
        }
    }

    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            loadSmsData()
        } else {
            smsPermissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun loadSmsData() {
        val transactions = SmsReader.readAllTransactions(this)
        if (transactions.isNotEmpty()) {
            lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val dao = db.transactionDao()
                // 去重插入
                for (t in transactions) {
                    if (dao.findBySmsBody(t.smsBody) == null) {
                        dao.insert(t)
                    }
                }
            }
            Toast.makeText(this, "读取到 ${transactions.size} 条账单", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "未找到账单短信", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    onRequestSms: () -> Unit,
    db: AppDatabase
) {
    var currentTab by remember { mutableIntStateOf(0) }
    val dao = db.transactionDao()
    val scope = rememberCoroutineScope()

    // 触发数据加载
    LaunchedEffect(Unit) {
        onRequestSms()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RMB记账") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, "明细") },
                    label = { Text("明细") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PieChart, "统计") },
                    label = { Text("统计") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarMonth, "年度") },
                    label = { Text("年度") },
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, "设置") },
                    label = { Text("设置") },
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentTab) {
                0 -> TransactionListScreen(dao)
                1 -> StatisticsScreen(dao)
                2 -> YearlyScreen(dao)
                3 -> SettingsScreen(
                    onRefresh = { onRequestSms() },
                    onClearAll = {
                        scope.launch {
                            dao.deleteAll()
                        }
                    }
                )
            }
        }
    }
}
