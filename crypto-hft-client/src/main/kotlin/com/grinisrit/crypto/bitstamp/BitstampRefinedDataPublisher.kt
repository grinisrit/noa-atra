package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.*

class BitstampRefinedDataPublisher: RefinedDataPublisher {
    override fun orderBookFlow(rawDataFlow: RawDataFlow): Flow<OrderBook> =
        rawDataFlow.filter {
            it.platform_data is BitstampOrderBook
        }.map {
            (it.platform_data as BitstampOrderBook).toOrderBook()
        }

    override fun tradeFlow(rawDataFlow: RawDataFlow): Flow<Trade> =
        rawDataFlow.filter {
            it.platform_data is BitstampTrade
        }.map {
            (it.platform_data as BitstampTrade).toTrade()
        }
}