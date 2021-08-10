package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.RawDataFlow
import com.grinisrit.crypto.common.RefinedDataPublisherSU
import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.OrderBook
import com.grinisrit.crypto.common.models.Trade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.time.Instant

class CoinbaseRefinedDataPublisher: RefinedDataPublisherSU {
     fun orderBookFlowS(snapshots: List<TimestampedMarketData>): Flow<OrderBook> {
        val snapshotsOrderBooks = snapshots.filter {
            it.platform_data is CoinbaseSnapshot
        }.map {
            (it.platform_data as CoinbaseSnapshot).toOrderBook()
        }
        return snapshotsOrderBooks.asFlow()
    }

    override fun orderBookFlow(snapshots: List<TimestampedMarketData>, updates: RawDataFlow): Flow<OrderBook> = flow {
        val snapshotsDatetime = mutableListOf<Instant>()
        val snapshotsOrderBooks = snapshots.filter {
            it.platform_data is CoinbaseSnapshot // TODO()
        }.map {
            snapshotsDatetime.add(it.receiving_datetime)
            (it.platform_data as CoinbaseSnapshot).toOrderBook()
        }
        var curIndex = 0
        var orderBook = snapshotsOrderBooks[curIndex]
        var nextDatetime = snapshotsDatetime.getOrNull(curIndex + 1)
        // TODO
        updates.collect {
            if ((nextDatetime != null) && (it.receiving_datetime >= nextDatetime)) {
                curIndex += 1
                orderBook = snapshotsOrderBooks[curIndex]
                nextDatetime = snapshotsDatetime.getOrNull(curIndex + 1)
            }
            orderBook = orderBook.update(it.platform_data as CoinbaseL2Update)
            emit(orderBook)
        }
    }

    override fun tradeFlow(rawDataFlow: RawDataFlow): Flow<Trade> = TODO()
    /*
        rawDataFlow.filter {
            it.platform_data is CoinbaseMatch
        }.map {
            (it.platform_data as CoinbaseMatch).toTrade()
        }

     */
}