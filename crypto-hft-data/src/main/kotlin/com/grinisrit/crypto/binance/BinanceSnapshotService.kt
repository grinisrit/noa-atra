package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.logger
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

import java.time.Instant
import kotlin.io.use

fun BinancePlatform.createBinanceSnapshots() =
    BinanceSnapshotService(this)

class BinanceSnapshotService internal constructor(
    val platform: BinancePlatform,
) {

    private val symbolToLastUpdateId: MutableMap<String, Long> = mutableMapOf()

    private suspend fun getSnapshot(symbol: String): RawMarketData {

        val snapshot: String = HttpClient().use {
            it.get("${platform.apiAddress}/depth?symbol=$symbol&limit=100") // TODO
        }
        return MarketDataParser.dataStringOf(platform.name, Instant.now(), snapshot)
    }

    private fun filterBookUpdate(bookUpdate: BookUpdate): Boolean {
        val symbol = bookUpdate.symbol
        val flag = (symbolToLastUpdateId[symbol] != bookUpdate.firstUpdateId - 1)
        symbolToLastUpdateId[symbol] = bookUpdate.finalUpdateId
        return flag
    }

    fun getFlow(marketDataFlow: MarkedDataFlow): RawMarketDataFlow =
        marketDataFlow
            .filter { it is BinanceData }
            .filter { it.platform_data is BookUpdate}
            .map {it.platform_data as BookUpdate}
            .filter { filterBookUpdate(it) }
            .map{ getSnapshot(it.symbol) }
            .catch { e ->
                logger.error(e) { "Failed to fetch snapshot from Binance" }
                delay(1000)
            }

    }


