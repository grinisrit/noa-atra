package com.grinisrit.crypto.common

import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

typealias RawDataFlow = Flow<TimestampedMarketData>
typealias RawDataSharedFlow = SharedFlow<TimestampedMarketData>



interface RefinedDataPublisher {

    fun handleOrderBook(marketDataFlow: RawDataFlow): Flow<OrderBook>
    fun handleTrade(marketDataFlow: RawDataFlow): Flow<Trade>

    fun orderBookFlow(rawDataFlow: RawDataFlow): Flow<OrderBook> = handleOrderBook(rawDataFlow)
    fun tradeFlow(rawDataFlow: RawDataFlow): Flow<Trade> = handleTrade(rawDataFlow)

}