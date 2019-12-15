package com.example.cryptocalculator.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class TradingPairs (@SerializedName("data") val data: List<TradePair>)

@Parcelize
data class TradePair(
    @SerializedName("symbol1") val symbol1: String,
    @SerializedName("symbol2") val symbol2: String,
    @SerializedName("lprice") var lprice: String
) : Parcelable