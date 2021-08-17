package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.Instant

object KrakenRefinedDataPublisher : RefinedDataPublisherSU {

    override fun orderBookFlow(
        snapshotsList: List<TimestampedMarketData>,
        unrefinedDataFlow: unrefinedDataFlow
    ): Flow<OrderBook> = flow {
        val snapshotsDatetime = mutableListOf<Instant>()
        val snapshotsOrderBooks = snapshotsList.filter {
            it.platform_data is KrakenBookSnapshot // TODO()
        }.map {
            snapshotsDatetime.add(it.receiving_datetime)
            (it.platform_data as KrakenBookSnapshot).toOrderBook(it.receiving_datetime)
        }
        var curIndex = 0
        var orderBook = snapshotsOrderBooks[curIndex]
        var nextDatetime = snapshotsDatetime.getOrNull(curIndex + 1)
        // TODO
        unrefinedDataFlow.collect {
            if ((nextDatetime != null) && (it.receiving_datetime >= nextDatetime)) {
                curIndex += 1
                orderBook = snapshotsOrderBooks[curIndex]
                nextDatetime = snapshotsDatetime.getOrNull(curIndex + 1)
            }
            orderBook = orderBook.update(it.platform_data as KrakenBookUpdate, it.receiving_datetime)
            emit(orderBook)
        }
    }

    @OptIn(FlowPreview::class)
    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> = // TODO()
        unrefinedDataFlow.flatMapConcat {
            (it.platform_data as KrakenTrade).tradeData.map { trade -> trade.toTrade() }.asFlow()
        }
}


