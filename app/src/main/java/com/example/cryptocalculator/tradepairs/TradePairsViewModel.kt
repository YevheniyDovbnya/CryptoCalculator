package com.example.cryptocalculator.tradepairs

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import com.example.cryptocalculator.data.Result
import com.example.cryptocalculator.data.TradePairsRepository
import com.example.cryptocalculator.data.TradePair
import kotlinx.coroutines.*


class TradePairsViewModel(
    application: Application,
    private val tradePairsRepository: TradePairsRepository = TradePairsRepository(application)
) : AndroidViewModel(application) {

    private val _repeatingTasks = RepeatingTasksLiveData()
    val repeatingTasks: LiveData<Boolean> = _repeatingTasks

    private val _tradingPairs = MutableLiveData<List<TradePair>>()
    val tradingPairs: LiveData<List<TradePair>> = _tradingPairs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _currencyId = MutableLiveData<String>()
    val currencyId: LiveData<Int> = Transformations.map(_currencyId, Function<String, Int> {
        return@Function when (it) {
            "USD" -> 0
            "EUR" -> 1
            else -> 2
        }
    });

    fun getTradePairs(isLoadingShow: Boolean) {
        viewModelScope.launch {
            _isLoading.value = isLoadingShow
            val result = tradePairsRepository.getTradePairs()
            if (result is Result.Success) {
                _tradingPairs.value = result.data
            } else {
                _error.value = (result as Result.Error).exception.message
            }
            _isLoading.value = false
        }
    }

    fun saveCurrency(currency: String) = viewModelScope.launch {
        tradePairsRepository.saveCurrency(currency)
    }

    fun getCurrency() = viewModelScope.launch {
        _currencyId.value = tradePairsRepository.getCurrency()
    }
}