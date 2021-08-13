package com.grinisrit.crypto.common

import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

typealias unrefinedDataFlow = Flow<TimestampedMarketData>
typealias RawDataSharedFlow = SharedFlow<TimestampedMarketData>



interface RefinedDataPublisher {

    fun orderBookFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<OrderBook>
    fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade>

}

interface RefinedDataPublisherSU {

    fun orderBookFlow(snapshotsList: List<TimestampedMarketData>, unrefinedDataFlow: unrefinedDataFlow): Flow<OrderBook>
    fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade>

}