package com.rmb.app.util

import android.content.Context
import android.net.Uri
import com.rmb.app.data.Transaction
import com.rmb.app.parser.SmsParser

object SmsReader {

    fun readAllTransactions(context: Context): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val uri = Uri.parse("content://sms/inbox")
        val cursor = context.contentResolver.query(
            uri,
            arrayOf("_id", "address", "body", "date"),
            null,
            null,
            "date DESC"
        )

        cursor?.use {
            val addressIdx = it.getColumnIndex("address")
            val bodyIdx = it.getColumnIndex("body")
            val dateIdx = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val address = it.getString(addressIdx) ?: ""
                val body = it.getString(bodyIdx) ?: ""
                val date = it.getLong(dateIdx)

                if (SmsParser.isTransactionSms(address, body)) {
                    val transaction = SmsParser.parse(address, body, date)
                    if (transaction != null) {
                        transactions.add(transaction)
                    }
                }
            }
        }

        return transactions
    }

    fun readNewTransactions(context: Context, sinceTimestamp: Long): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val uri = Uri.parse("content://sms/inbox")
        val selection = "date > ?"
        val selectionArgs = arrayOf(sinceTimestamp.toString())
        val cursor = context.contentResolver.query(
            uri,
            arrayOf("_id", "address", "body", "date"),
            selection,
            selectionArgs,
            "date DESC"
        )

        cursor?.use {
            val addressIdx = it.getColumnIndex("address")
            val bodyIdx = it.getColumnIndex("body")
            val dateIdx = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val address = it.getString(addressIdx) ?: ""
                val body = it.getString(bodyIdx) ?: ""
                val date = it.getLong(dateIdx)

                if (SmsParser.isTransactionSms(address, body)) {
                    val transaction = SmsParser.parse(address, body, date)
                    if (transaction != null) {
                        transactions.add(transaction)
                    }
                }
            }
        }

        return transactions
    }
}
