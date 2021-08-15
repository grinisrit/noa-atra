package com.grinisrit.crypto.finery

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.*
import java.time.Instant

object FineryRefinedDataPublisher: RefinedDataPublisherSU {

    override fun orderBookFlow(
        snapshotsList: List<TimestampedMarketData>,
        unrefinedDataFlow: unrefinedDataFlow
    ): Flow<OrderBook> = flow {
        val snapshotsDatetime = mutableListOf<Instant>()
        val snapshotsOrderBooks = snapshotsList.filter {
            it.platform_data is FinerySnapshot // TODO()
        }.map {
            snapshotsDatetime.add(it.receiving_datetime)
            (it.platform_data as FinerySnapshot).toOrderBook(it.receiving_datetime)
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
            orderBook = orderBook.update(it.platform_data as FineryUpdates, it.receiving_datetime)
            emit(orderBook)
        }
    }

    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> = TODO()
}


