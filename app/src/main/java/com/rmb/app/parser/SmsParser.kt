package com.rmb.app.parser

import com.rmb.app.data.Transaction
import java.util.Calendar
import java.util.regex.Pattern

object SmsParser {

    // 银行短信规则
    private val bankPatterns = listOf(
        // 招商银行
        BankPattern(
            bankName = "招商银行",
            patterns = listOf(
                // 消费: 您的招商银行信用卡****1234于07月01日消费人民币100.00元
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元?.*?(?:于|在)\\s*(.+?)(?:\\s|$)"),
                // 收入: 您的储蓄卡****1234于07月01日收入人民币100.00元
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                // 通用金额
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("[*×]{4}(\\d{4})"),
            tailPattern = Pattern.compile("(?:卡号|尾号|\\*{4}|×{4})(\\d{4})")
        ),
        // 工商银行
        BankPattern(
            bankName = "工商银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 建设银行
        BankPattern(
            bankName = "建设银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 农业银行
        BankPattern(
            bankName = "农业银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 中国银行
        BankPattern(
            bankName = "中国银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 交通银行
        BankPattern(
            bankName = "交通银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 邮储银行
        BankPattern(
            bankName = "邮储银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 浦发银行
        BankPattern(
            bankName = "浦发银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 民生银行
        BankPattern(
            bankName = "民生银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        ),
        // 光大银行
        BankPattern(
            bankName = "光大银行",
            patterns = listOf(
                Pattern.compile("(?:消费|支出|扣款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)")
            ),
            cardPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})"),
            tailPattern = Pattern.compile("(?:尾号|\\*{4})(\\d{4})")
        )
    )

    // 支付宝规则
    private val alipayPatterns = listOf(
        // 支付宝消费: 【支付宝】您于07月01日12:00消费100.00元，余额1000.00元
        Pattern.compile("(?:消费|支出|扣款|付款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
        // 支付宝收入: 【支付宝】您于07月01日收到转账100.00元
        Pattern.compile("(?:收到|收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元")
    )

    // 微信支付规则
    private val wechatPatterns = listOf(
        // 微信支付: 【微信支付】您于07月01日消费100.00元
        Pattern.compile("(?:消费|支出|扣款|付款).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
        // 微信收入: 【微信支付】您收到一笔转账100.00元
        Pattern.compile("(?:收到|收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元")
    )

    // 商户关键词分类
    private val categoryKeywords = mapOf(
        "餐饮" to listOf("美团", "饿了么", "肯德基", "麦当劳", "星巴克", "瑞幸", "海底捞", "外卖", "餐厅", "饭店", "食堂", "小吃", "烧烤", "火锅"),
        "交通" to listOf("滴滴", "高德", "地铁", "公交", "出租车", "加油", "停车", "高速", "铁路", "航空", "机票", "火车票"),
        "购物" to listOf("淘宝", "天猫", "京东", "拼多多", "唯品会", "苏宁", "国美", "沃尔玛", "超市", "商场", "百货"),
        "娱乐" to listOf("爱奇艺", "腾讯视频", "优酷", "网易云", "QQ音乐", "电影", "游戏", "Steam"),
        "通讯" to listOf("中国移动", "中国联通", "中国电信", "话费", "流量", "宽带"),
        "住房" to listOf("房租", "物业", "水费", "电费", "燃气", "暖气", "房贷"),
        "医疗" to listOf("医院", "药店", "诊所", "体检", "牙科", "眼科"),
        "教育" to listOf("学费", "培训", "课程", "书店", "教材"),
        "金融" to listOf("理财", "基金", "股票", "保险", "利息", "还款", "信用卡"),
        "转账" to listOf("转账", "红包", "收款", "付款")
    )

    data class ParseResult(
        val amount: Double,
        val type: Int,          // 0=支出, 1=收入
        val category: String,
        val source: String,     // 银行卡/支付宝/微信
        val bankName: String,
        val cardTail: String,
        val merchant: String
    )

    fun parse(address: String, body: String, timestamp: Long): Transaction? {
        val result = parseResult(address, body) ?: return null

        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

        return Transaction(
            amount = result.amount,
            type = result.type,
            category = result.category,
            description = body.take(50),
            source = result.source,
            bankName = result.bankName,
            cardTail = result.cardTail,
            merchant = result.merchant,
            smsBody = body,
            timestamp = timestamp,
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun parseResult(address: String, body: String): ParseResult? {
        // 识别来源
        val source = identifySource(address, body)
        if (source == "unknown") return null

        // 提取金额
        val amount = extractAmount(body, source) ?: return null

        // 判断收入/支出
        val type = identifyType(body)

        // 提取商户
        val merchant = extractMerchant(body, source)

        // 分类
        val category = categorize(body, merchant, source)

        // 提取银行卡信息
        val bankName = extractBankName(address, body, source)
        val cardTail = extractCardTail(body)

        return ParseResult(
            amount = amount,
            type = type,
            category = category,
            source = source,
            bankName = bankName,
            cardTail = cardTail,
            merchant = merchant
        )
    }

    private fun identifySource(address: String, body: String): String {
        val lower = (address + " " + body).lowercase()
        return when {
            lower.contains("支付宝") || lower.contains("alipay") -> "支付宝"
            lower.contains("微信") || lower.contains("wechat") || lower.contains("财付通") -> "微信"
            lower.contains("银行") || lower.contains("bank") || lower.contains("credit") -> "银行卡"
            // 根据发送方号码判断
            address.startsWith("106") && (lower.contains("消费") || lower.contains("支出") || lower.contains("到账")) -> "银行卡"
            else -> "unknown"
        }
    }

    private fun extractAmount(body: String, source: String): Double? {
        // 通用金额提取
        val patterns = when (source) {
            "支付宝" -> alipayPatterns
            "微信" -> wechatPatterns
            else -> {
                // 银行卡，找第一个匹配的银行规则
                for (bank in bankPatterns) {
                    if (body.contains(bank.bankName) || body.contains(bank.bankName.replace("银行", ""))) {
                        for (p in bank.patterns) {
                            val m = p.matcher(body)
                            if (m.find()) {
                                return parseAmount(m.group(1))
                            }
                        }
                    }
                }
                // 通用银行模式
                listOf(
                    Pattern.compile("(?:消费|支出|扣款|收入|到账|转入).*?(?:人民币|CNY|¥)?\\s*([\\d,.]+)\\s*元"),
                    Pattern.compile("(?:人民币|CNY|¥)\\s*([\\d,.]+)"),
                    Pattern.compile("([\\d,.]+)\\s*元")
                )
            }
        }

        for (p in patterns) {
            val m = p.matcher(body)
            if (m.find()) {
                return parseAmount(m.group(1))
            }
        }
        return null
    }

    private fun parseAmount(s: String?): Double? {
        if (s == null) return null
        return try {
            s.replace(",", "").toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun identifyType(body: String): Int {
        val incomeKeywords = listOf("收入", "到账", "转入", "收款", "收到", "红包", "退款", "利息", "工资", "奖金")
        val expenseKeywords = listOf("消费", "支出", "扣款", "付款", "转账", "缴费", "还款")

        val lower = body.lowercase()
        return when {
            incomeKeywords.any { lower.contains(it) } -> 1
            expenseKeywords.any { lower.contains(it) } -> 0
            else -> 0 // 默认支出
        }
    }

    private fun extractMerchant(body: String, source: String): String {
        // 尝试提取商户名
        val merchantPatterns = listOf(
            Pattern.compile("(?:在|于)\\s*(.+?)(?:消费|支出|扣款|付款)"),
            Pattern.compile("(?:商户|商家)[:：]\\s*(.+?)(?:\\s|$)"),
            Pattern.compile("(?:消费于|支付给)\\s*(.+?)(?:\\s|$)")
        )

        for (p in merchantPatterns) {
            val m = p.matcher(body)
            if (m.find()) {
                return m.group(1)?.trim() ?: ""
            }
        }
        return ""
    }

    private fun categorize(body: String, merchant: String, source: String): String {
        val text = "$body $merchant".lowercase()
        for ((category, keywords) in categoryKeywords) {
            if (keywords.any { text.contains(it.lowercase()) }) {
                return category
            }
        }
        return "其他"
    }

    private fun extractBankName(address: String, body: String, source: String): String {
        if (source != "银行卡") return source
        for (bank in bankPatterns) {
            if (body.contains(bank.bankName)) {
                return bank.bankName
            }
        }
        return "其他银行"
    }

    private fun extractCardTail(body: String): String {
        val p = Pattern.compile("(?:尾号|\\*{4}|×{4})(\\d{4})")
        val m = p.matcher(body)
        return if (m.find()) m.group(1) ?: "" else ""
    }

    // 判断是否是账单类短信（过滤掉广告、验证码等）
    fun isTransactionSms(address: String, body: String): Boolean {
        val keywords = listOf("消费", "支出", "扣款", "收入", "到账", "转入", "付款", "收款", "转账", "红包", "退款", "还款", "缴费", "充值", "提现")
        return keywords.any { body.contains(it) } &&
               (body.contains("元") || body.contains("¥") || body.contains("CNY"))
    }

    private data class BankPattern(
        val bankName: String,
        val patterns: List<Pattern>,
        val cardPattern: Pattern,
        val tailPattern: Pattern
    )
}
