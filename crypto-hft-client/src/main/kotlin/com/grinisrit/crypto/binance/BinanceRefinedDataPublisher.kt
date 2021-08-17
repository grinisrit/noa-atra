package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.*

object BinanceRefinedDataPublisher: RefinedDataPublisherSU {

    override fun orderBookFlow(
        snapshotsList: List<TimestampedMarketData>,
        unrefinedDataFlow: unrefinedDataFlow
    ): Flow<OrderBook> = flow {
        val snapshotsLastUpdateId = mutableListOf<Long>()
        val snapshotsOrderBooks = snapshotsList.filter {
            it.platform_data is BinanceSnapshot // TODO()
        }.map {
            it.platform_data as BinanceSnapshot
            snapshotsLastUpdateId.add((it.platform_data as BinanceSnapshot).snapshot.lastUpdateId)
            (it.platform_data as BinanceSnapshot).toOrderBook()
        }
        var curIndex = 0
        var orderBook = snapshotsOrderBooks[curIndex]
        var curSnapshotLastUpdateId: Long? = snapshotsLastUpdateId[curIndex]
        var lastLastUpdateId: Long? = null
        // TODO
        unrefinedDataFlow.collect {
            val update = it.platform_data as BinanceBookUpdate // TODO
            if (lastLastUpdateId == null){
                if (curSnapshotLastUpdateId == null){
                    return@collect
                }
                if ((update.firstUpdateId <= (curSnapshotLastUpdateId!! + 1)) &&
                    (update.finalUpdateId >= (curSnapshotLastUpdateId!! + 1))
                ) {
                    orderBook = snapshotsOrderBooks[curIndex]
                    lastLastUpdateId = update.finalUpdateId
                    curIndex += 1
                    curSnapshotLastUpdateId = snapshotsLastUpdateId.getOrNull(curIndex)
                }
            } else {
                if (update.firstUpdateId != lastLastUpdateId!! + 1){
                    lastLastUpdateId = null
                    return@collect
                }
                lastLastUpdateId = update.finalUpdateId
                orderBook = orderBook.update(it.platform_data as BinanceBookUpdate)
                emit(orderBook)
            }

        }
    }

    override fun tradeFlow(unrefinedDataFlow: unrefinedDataFlow): Flow<Trade> =
        unrefinedDataFlow.filter {
            it.platform_data is BinanceTrade
        }.map {
            (it.platform_data as BinanceTrade).toTrade()
        }
}
