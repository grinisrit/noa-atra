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

typealias MarketDataMutableFlow = MutableSharedFlow<TimestampedMarketData>
typealias MarketDataFlow = SharedFlow<TimestampedMarketData>
typealias RawMarketJsonMutableFlow = MutableSharedFlow<RawMarketJson>
typealias RawMarketJsonFlow = Flow<RawMarketJson>

fun CoroutineScope.createMarketDataBroker(conf: ConfYAMl): MarketDataBroker =
    MarketDataBroker.fromConfig(this, conf)

fun CoroutineScope.createServer(conf: ConfYAMl): MarketDataBroker =
    MarketDataBroker.createServer(this, conf)

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

        internal fun createServer(scope: CoroutineScope, conf: ConfYAMl): MarketDataBroker =
            MarketDataBroker(
                scope,
                null,
                MarketDataSubService(conf.zeromq)
            )
    }

    fun getFlow(): MarketDataFlow? = subService?.getFlow()

    suspend fun publishFlow(rawMarketJsonFlow: RawMarketJsonFlow): Unit? =
        pubService?.publishFlow(rawMarketJsonFlow)

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
    private val outFlow: MarketDataMutableFlow = MutableSharedFlow()
    private val subInfo = "Subscribing to market data feed on ${zmqConfig.address}"
    private val subError = "Failed to consume market data from ${zmqConfig.address}"

    fun getFlow(): MarketDataFlow = outFlow.asSharedFlow()

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
    private val inFlow: RawMarketJsonMutableFlow = MutableSharedFlow()
    private val pubInfo = "Publishing market data on ${zmqConfig.address}"
    private val pubError = "Failed to launch market data publication on ${zmqConfig.address}"

    suspend fun publishFlow(rawMarketJsonFlow: RawMarketJsonFlow) =
        rawMarketJsonFlow.collect { inFlow.emit(it) }

    fun launchPubService(context: ZContext, logger: KLogger): Flow<RawMarketJson> {
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

