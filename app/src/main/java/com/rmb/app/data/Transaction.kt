package com.rmb.app.data

import androidx.room.*

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,           // 金额
    val type: Int,                // 0=支出, 1=收入
    val category: String,         // 分类
    val description: String,      // 描述
    val source: String,           // 来源（银行卡/支付宝/微信等）
    val bankName: String,         // 银行名称
    val cardTail: String,         // 尾号
    val merchant: String,         // 商户名
    val smsBody: String,          // 原始短信
    val timestamp: Long,          // 短信时间戳
    val year: Int,
    val month: Int,
    val day: Int
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE year = :year ORDER BY timestamp DESC")
    suspend fun getByYear(year: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE year = :year AND month = :month ORDER BY timestamp DESC")
    suspend fun getByMonth(year: Int, month: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getByType(type: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    suspend fun getByCategory(category: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE year = :year AND month = :month AND type = :type")
    suspend fun getByYearMonthType(year: Int, month: Int, type: Int): List<Transaction>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE year = :year AND month = :month AND type = :type GROUP BY category ORDER BY total DESC")
    suspend fun getCategorySummary(year: Int, month: Int, type: Int): List<CategorySummary>

    @Query("SELECT DISTINCT year FROM transactions ORDER BY year DESC")
    suspend fun getAvailableYears(): List<Int>

    @Query("SELECT DISTINCT month FROM transactions WHERE year = :year ORDER BY month")
    suspend fun getAvailableMonths(year: Int): List<Int>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getCount(): Int

    @Query("SELECT SUM(amount) FROM transactions WHERE year = :year AND month = :month AND type = :type")
    suspend fun getMonthTotal(year: Int, month: Int, type: Int): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE year = :year AND type = :type")
    suspend fun getYearTotal(year: Int, type: Int): Double?

    @Query("SELECT * FROM transactions WHERE smsBody = :smsBody LIMIT 1")
    suspend fun findBySmsBody(smsBody: String): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

data class CategorySummary(
    val category: String,
    val total: Double
)

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rmb_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
