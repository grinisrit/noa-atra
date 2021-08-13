package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.unrefinedDataFlow
import com.grinisrit.crypto.common.RefinedDataPublisherSU
import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.OrderBook
import com.grinisrit.crypto.common.models.Trade
import kotlinx.coroutines.flow.*
import java.time.Instant

class CoinbaseRefinedDataPublisher: RefinedDataPublisherSU {

    override fun orderBookFlow(
        snapshotsList: List<TimestampedMarketData>,
        unrefinedDataFlow: unrefinedDataFlow
    ): Flow<OrderBook> = flow {
        val snapshotsDatetime = mutableListOf<Instant>()
        val snapshotsOrderBooks = snapshotsList.filter {
            it.platform_data is CoinbaseSnapshot // TODO()
        }.map {
            snapshotsDatetime.add(it.receiving_datetime)
            (it.platform_data as CoinbaseSnapshot).toOrderBook()
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
            orderBook = orderBook.update(it.platform_data as CoinbaseL2Update)
            emit(orderBook)
        }
    }

    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> =
        unrefinedDataFlow.filter {
            it.platform_data is CoinbaseMatch
        }.map {
            (it.platform_data as CoinbaseMatch).toTrade()
        }
}
