package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.commonLogger
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import mu.KotlinLogging

import java.time.Instant
import kotlin.io.use

fun BinancePlatform.createBinanceSnapshots() =
    BinanceSnapshotService(this)

class BinanceSnapshotService internal constructor(
    val platform: BinancePlatform,
    private val dropFirst: Int = 10,
    private val bookDepth: Int = 1000,
    private val delayOnFailure: Long = 1000
) {
    private val logger = KotlinLogging.logger { }

    private fun debugLog(msg: String) = logger.debug { "${platform.name} ws: $msg" }

    private val symbolToLastUpdateId: MutableMap<String, Long> = mutableMapOf()

    private suspend fun getSnapshot(symbol: String): RawMarketJson {

        debugLog("Trying to get snapshot for $symbol")

        val snapshot: String = HttpClient().use {
            it.get("${platform.apiAddress}/depth?symbol=$symbol&limit=${bookDepth}")
        }

        debugLog("Got snapshot for $symbol")

        return MarketDataParser.dataStringOf(platform.name, Instant.now(),
            "{\"snapshot\":$snapshot,\"symbol\":\"$symbol\"}")
    }

    private fun filterBookUpdate(bookUpdate: BinanceBookUpdate): Boolean {
        val symbol = bookUpdate.symbol
        val flag = (symbolToLastUpdateId[symbol] != bookUpdate.firstUpdateId - 1)
        symbolToLastUpdateId[symbol] = bookUpdate.finalUpdateId
        return flag
    }

    fun getFlow(marketDataFlow: MarketDataFlow): RawMarketJsonFlow =
        marketDataFlow
            .filter { it.platform_data is BinanceData }
            .filter { it.platform_data is BinanceBookUpdate}
            .drop(dropFirst)
            .map {it.platform_data as BinanceBookUpdate}
            .filter { filterBookUpdate(it) }
            .map{ getSnapshot(it.symbol) }
            .catch { e ->
                commonLogger.error(e) { "Failed to fetch snapshot from Binance" }
                delay(delayOnFailure)
            }
    }


