package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.RawDataFlow
import com.grinisrit.crypto.common.RefinedDataPublisher
import com.grinisrit.crypto.common.models.OrderBook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class BitstampRefinedDataPublisher: RefinedDataPublisher {
    override fun handleOrderBook(marketDataFlow: RawDataFlow): Flow<OrderBook> =
        marketDataFlow.filter {
            it.platform_data is BitstampOrderBook
        }.map {
            (it.platform_data as BitstampOrderBook).toOrderBook()
        }
}