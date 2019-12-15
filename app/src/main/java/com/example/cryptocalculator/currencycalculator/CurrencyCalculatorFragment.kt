package com.example.cryptocalculator.currencycalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.example.cryptocalculator.R
import com.example.cryptocalculator.data.TradePair
import kotlinx.android.synthetic.main.fragment_currency_calculator.*


class CurrencyCalculatorFragment : Fragment() {

    companion object {
        fun newInstance() = CurrencyCalculatorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currency_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tradePair = arguments?.getParcelable<TradePair>("tradePair")
        tradePair?.let {
            tradePairTextView.text = getString(R.string.trade_pair_title, it.symbol1, it.symbol2)
            symbol1TextView.text = it.symbol1
            symbol2TextView.text = it.symbol2
            priceTextView.text = it.lprice
        }
        swapView.setOnClickListener {

            val tempSymbol = symbol1TextView.text
            symbol1TextView.text = symbol2TextView.text
            symbol2TextView.text = tempSymbol

            val tempValue = priceTextView.text
            priceTextView.text = amountEditText.text
            amountEditText.setText(tempValue)
        }
        amountEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sumTextView.text = ""
                sumTextView.text = (priceTextView.text.toString().toFloat() * amountEditText.text.toString().toFloat()).toString()
                true
            }
            false
        }
    }
}
