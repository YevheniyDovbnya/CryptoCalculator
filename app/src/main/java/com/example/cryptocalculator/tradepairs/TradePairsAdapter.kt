package com.example.cryptocalculator.tradepairs

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.example.cryptocalculator.R
import com.example.cryptocalculator.data.TradePair
import kotlinx.android.synthetic.main.header_trade_pairs.view.*

import kotlinx.android.synthetic.main.item_trade_pair.view.*

class TradePairsAdapter(
    private val values: MutableList<TradePair>,
    private val itemClickListener: (TradePair) -> Unit,
    private val currencyChangeListener: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userSelect = false

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER_VIEW_TYPE
            else -> ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> HeaderViewHolder(layoutInflater.inflate(R.layout.header_trade_pairs, parent, false))
            else -> ItemViewHolder(layoutInflater.inflate(R.layout.item_trade_pair, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.currencySpinnerView.setOnTouchListener { _, _ ->
                    userSelect = true
                    false
                }
                holder.currencySpinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (userSelect) {
                            currencyChangeListener.invoke(parent.getItemAtPosition(position).toString())
                            userSelect = false
                        }
                    }

                }
            }
            is ItemViewHolder -> {
                val item = values[position - 1]
                with(holder) {
                    currencyTextView.text = item.symbol1
                    priceTextView.text = item.lprice
                    itemView.setOnClickListener {
                        itemClickListener.invoke(item)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && holder is ItemViewHolder && payloads[0] == Payloads.PAYLOAD_PRICE_CHANGE) {
            with(holder) {
                priceTextView.text = values[position - 1].lprice
                priceTextView.startAnimation(AnimationUtils
                    .loadAnimation(holder.itemView.priceTextView.context, android.R.anim.slide_in_left))
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = values.size + 1

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencySpinnerView: Spinner = itemView.currencySpinnerView
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val currencyTextView: TextView = itemView.currencyTextView
        val priceTextView: TextView = itemView.priceTextView
    }

    companion object {
        private const val HEADER_VIEW_TYPE = 0
        private const val ITEM_VIEW_TYPE = 1
    }

    enum class Payloads {
        PAYLOAD_PRICE_CHANGE
    }

    fun addItems(items: List<TradePair>) {
        values.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItems(items: List<TradePair>) {
        items.forEachIndexed { index, newValue ->
            val oldValue = values[index]
            if (newValue.lprice != oldValue.lprice) {
                oldValue.lprice = newValue.lprice
                notifyItemChanged(index + 1, Payloads.PAYLOAD_PRICE_CHANGE)
            }
        }
    }

    fun clearItems() = values.clear()
}
