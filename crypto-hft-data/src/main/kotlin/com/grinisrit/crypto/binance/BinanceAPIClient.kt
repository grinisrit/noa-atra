package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.DataTime
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.kraken.UpdateData
import io.ktor.client.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newSingleThreadContext
import org.litote.kmongo.KMongo
import org.litote.kmongo.*
import org.zeromq.ZMQ
import java.time.Instant
import kotlin.io.use

class BinanceAPIClient(
    val platform: BinancePlatform,
    val socket: ZMQ.Socket
) {

    private val col = KMongo.createClient().getDatabase(platform.platformName)
        .getCollection<DataTime<Snapshot>>("snapshot")

    private val symbolToLastUpdateId: MutableMap<String, Long> = mutableMapOf()

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun subscriptionFlow() = flow {
        while (true) {
            val data = socket.recvStr() ?: continue
            emit(data)
        }
    }.flowOn(newSingleThreadContext("BinanceAPIZeroMQSub"))

    private suspend fun getSnapshot(symbol: String) {

        // todo reconnect
        val snapshot: String = HttpClient().use {
            it.get("https://api.binance.com/api/v3/depth?symbol=$symbol&limit=1000") // TODO
        }
        col.insertOne(
            DataTime(
                Instant.now(),
                DataTransport.decodeJsonData(snapshot, Snapshot.serializer())
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

    @OptIn(DelicateCoroutinesApi::class)
    fun run() {
        subscriptionFlow().onEach {
            val dataTime = DataTransport.fromDataString(it, BinanceDataSerializer)
            println(dataTime.data.type)
            (dataTime.data as? BookUpdate)?.let { bookUpdate ->
                handleBookUpdate(bookUpdate)
            }
        }.launchIn(GlobalScope) // TODO
    }
}