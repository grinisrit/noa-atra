package com.grinisrit.crypto.common

interface MarketDataSource {
    fun getFlow(): RawDataFlow
}

interface MarketDataSharedSource {
    fun getFlow(): RawDataSharedFlow
}