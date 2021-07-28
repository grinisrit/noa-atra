package com.grinisrit.crypto.common

import com.grinisrit.crypto.ConfYAMl
import com.grinisrit.crypto.ZeroMQConfig

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.lang.RuntimeException

typealias MutableTimestampedDataFlow = MutableSharedFlow<TimestampedData>
typealias TimestampedDataFlow = SharedFlow<TimestampedData>
typealias MutableJsonStringDataFlow = MutableSharedFlow<JsonStringData>
typealias JsonStringDataFlow = Flow<JsonStringData>

fun CoroutineScope.createMarketDataBroker(conf: ConfYAMl): MarketDataBroker =
    MarketDataBroker.fromConfig(this, conf)


class MarketDataBroker private constructor(
    private val coroutineScope: CoroutineScope,
    private val pubService: MarketDataPubService?,
    private val subService: MarketDataSubService?
) {

    companion object {
        internal fun fromConfig(scope: CoroutineScope, conf: ConfYAMl): MarketDataBroker =
            MarketDataBroker(
                scope,
                with(conf.platforms) {
                    if (coinbase.isOn or binance.isOn or bitstamp.isOn or kraken.isOn or deribit.isOn or finery.isOn)
                        MarketDataPubService(conf.zeromq) else null
                },
                if (conf.mongodb.isOn or conf.platforms.binance.isOn)
                    MarketDataSubService(conf.zeromq) else null
            )
    }

    fun getFlow(): TimestampedDataFlow? = subService?.getFlow()

    suspend fun publishFlow(jsonStringDataFlow: JsonStringDataFlow): Unit? =
        pubService?.publishFlow(jsonStringDataFlow)

    fun launchBroker(): Job =
        coroutineScope.launch(Dispatchers.IO) {
            val logger = KotlinLogging.logger { }
            ZContext().use { context ->
                val pubJob = pubService?.launchPubService(context, logger)?.launchIn(this)
                subService?.launchSubService(context, logger)
                pubJob?.join()
            }
        }
}


// None of those classes shall appear anywhere else

private sealed class ZMQService {
    protected var socket: ZMQ.Socket? = null
}

private class MarketDataSubService(val zmqConfig: ZeroMQConfig) : ZMQService() {
    private val outFlow: MutableTimestampedDataFlow = MutableSharedFlow()
    private val subInfo = "Subscribing to market data feed on ${zmqConfig.address}"
    private val subError = "Failed to consume market data from ${zmqConfig.address}"

    fun getFlow(): TimestampedDataFlow = outFlow.asSharedFlow()

    suspend fun launchSubService(context: ZContext, logger: KLogger) {
        val socket = context.getSubSocket(zmqConfig, logger)
        for (rawData in socket.recvStrStream())
            try {
                outFlow.emit(MarketDataParser.parseRawMarketData(rawData))
            } catch (e: Throwable) {
                logger.error(e) { "Received corrupted market data" }
            }
    }

    private fun ZContext.getSubSocket(zmq: ZeroMQConfig, logger: KLogger): ZMQ.Socket =
        try {
            logger.debug { subInfo }
            createSocket(SocketType.SUB).apply {
                connect(zmq.address)
                subscribe("")
            }
        } catch (e: Throwable) {
            logger.error(e) { subError }
            throw RuntimeException(subError)
        }
}

private class MarketDataPubService(val zmqConfig: ZeroMQConfig) : ZMQService() {
    private val inFlow: MutableJsonStringDataFlow = MutableSharedFlow()
    private val pubInfo = "Publishing market data on ${zmqConfig.address}"
    private val pubError = "Failed to launch market data publication on ${zmqConfig.address}"

    suspend fun publishFlow(jsonStringDataFlow: JsonStringDataFlow) =
        jsonStringDataFlow.collect { inFlow.emit(it) }

    fun launchPubService(context: ZContext, logger: KLogger): Flow<JsonStringData> {
        socket = context.getPubSocket(zmqConfig, logger)
        return inFlow.onEach { rawData ->
            socket?.send(rawData)
        }
    }

    private fun ZContext.getPubSocket(zmq: ZeroMQConfig, logger: KLogger): ZMQ.Socket =
        try {
            logger.debug { pubInfo }
            createSocket(SocketType.PUB).apply { bind(zmq.address) }
        } catch (e: Throwable) {
            logger.error(e) { pubError }
            throw RuntimeException(pubError)
        }
}

