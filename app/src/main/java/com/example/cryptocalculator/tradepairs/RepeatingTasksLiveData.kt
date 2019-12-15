package com.example.cryptocalculator.tradepairs

import android.os.Handler
import androidx.lifecycle.MutableLiveData


class RepeatingTasksLiveData : MutableLiveData<Boolean>() {

    companion object {
        private const val REPEATING_PERIOD = 5000L
    }

    private val handler: Handler = Handler()

    private val runnable = object : Runnable {
        override fun run() {
            value = true
            handler.postDelayed(this, REPEATING_PERIOD)
        }
    }

    override fun onActive() {
        super.onActive()
        handler.post(runnable)

    }

    override fun onInactive() {
        super.onInactive()
        handler.removeCallbacks(runnable)
    }
}