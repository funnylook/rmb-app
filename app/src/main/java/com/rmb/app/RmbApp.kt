package com.rmb.app

import android.app.Application
import com.rmb.app.data.AppDatabase

class RmbApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
