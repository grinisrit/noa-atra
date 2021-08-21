package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

object DeribitRefinedDataPublisher: RefinedDataPublisher {
    override fun orderBookFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<OrderBook> =
        unrefinedDataFlow.filter {
            it.platform_data is DeribitBook
        }.map {
            (it.platform_data as DeribitBook).toOrderBook()
        }

    @OptIn(FlowPreview::class)
    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> =
        unrefinedDataFlow.flatMapConcat {
            (it.platform_data as DeribitTrades).params.data.map { trade -> trade.toTrade() }.asFlow()
        }
}