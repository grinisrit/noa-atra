package com.grinisrit.crypto.common

import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

typealias RawDataFlow = Flow<TimestampedMarketData>
typealias RawDataSharedFlow = SharedFlow<TimestampedMarketData>



interface RefinedDataPublisher {

    fun orderBookFlow(rawDataFlow: RawDataFlow): Flow<OrderBook>
    fun tradeFlow(rawDataFlow: RawDataFlow): Flow<Trade>

}

interface RefinedDataPublisherSU {

    fun orderBookFlow(snapshots:List<TimestampedMarketData>, updates: RawDataFlow): Flow<OrderBook>
    fun tradeFlow(rawDataFlow: RawDataFlow): Flow<Trade>

}