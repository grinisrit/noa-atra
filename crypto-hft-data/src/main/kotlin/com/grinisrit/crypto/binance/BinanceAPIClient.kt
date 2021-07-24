package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.MarketData
import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.litote.kmongo.coroutine.CoroutineClient
import java.time.Instant
import kotlin.io.use

class BinanceAPIClient(
    val platform: BinancePlatform,
    val dataFlow: Flow<String>,
    kMongoClient: CoroutineClient,
) {

    private val col = kMongoClient.getDatabase(platform.name)
        .getCollection<MarketData<Snapshot>>("snapshot")

    private val symbolToLastUpdateId: MutableMap<String, Long> = mutableMapOf()

    private suspend fun getSnapshot(symbol: String) {

        val snapshot: String = HttpClient().use {
            it.get("${platform.apiAddress}/depth?symbol=$symbol&limit=1000") // TODO
        }

        col.insertOne(
            MarketData(
                Instant.now(),
                Snapshot(
                    DataTransport.decodeJsonData(snapshot, SnapshotData.serializer()),
                    symbol
                )
            )
        )
    }

    private suspend fun handleBookUpdate(bookUpdate: BookUpdate) {
        val symbol = bookUpdate.symbol
        if (symbolToLastUpdateId[symbol] != bookUpdate.firstUpdateId - 1) {
            getSnapshot(symbol)
        }
        symbolToLastUpdateId[symbol] = bookUpdate.finalUpdateId
    }

    private suspend fun filterBinanceData(dataString: String) {
        val dataTime = DataTransport.fromDataString(dataString, BinanceDataSerializer)
        (dataTime.platform_data as? BookUpdate)?.let { bookUpdate ->
            try {
                handleBookUpdate(bookUpdate)
            } catch (e: Throwable) {
                println(e)
                delay(5000)
                // TODO log
            }
        }
    }

    suspend fun run() {
        dataFlow.collect {
            filterBinanceData(it)
        }
    }

}
