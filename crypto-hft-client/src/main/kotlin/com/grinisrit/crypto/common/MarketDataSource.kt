package com.grinisrit.crypto.common

interface MarketDataSource {
    fun getFlow(): unrefinedDataFlow
}

interface MarketDataSharedSource {
    fun getFlow(): RawDataSharedFlow
}