package com.example.cryptocalculator.data

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class TradePairsRepository(application: Application) {

    private val sharedPreferences = application.getSharedPreferences("com.example.cryptocalculator.data.CEO", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_CURRENCY = "com.example.cryptocalculator.data.PREF_CURRENCY"
    }

    suspend fun getTradePairs() = withContext(Dispatchers.IO) {
        try {
            val result = ApiManager.ceoApi.getLastPrices(getCurrency()!!).data
            if (result.isNotEmpty()) {
                return@withContext Result.Success(result)
            } else {
                return@withContext Result.Error(Exception("Last prices not found!"))
            }
        } catch (e: IOException) {
            return@withContext Result.Error(e)
        }
    }

    suspend fun saveCurrency(currency: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit(commit = true) {
            putString(PREF_CURRENCY, currency)
        }
    }

    suspend fun getCurrency() = withContext(Dispatchers.IO) {
        return@withContext sharedPreferences.getString(PREF_CURRENCY, "USD")
    }
}