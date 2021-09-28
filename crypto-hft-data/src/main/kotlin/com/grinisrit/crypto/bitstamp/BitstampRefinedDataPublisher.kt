package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.*

object BitstampRefinedDataPublisher: RefinedDataPublisher {
    override fun orderBookFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<OrderBook> =
        unrefinedDataFlow.filter {
            it.platform_data is BitstampOrderBook
        }.map {
            (it.platform_data as BitstampOrderBook).toOrderBook()
        }

    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> =
        unrefinedDataFlow.filter {
            it.platform_data is BitstampTrade
        }.map {
            (it.platform_data as BitstampTrade).toTrade()
        }
}