package com.grinisrit.crypto.common

import com.grinisrit.crypto.ConfYAMl
import com.grinisrit.crypto.ZeroMQConfig
import com.grinisrit.crypto.logger

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

typealias MutableMarkedDataFlow = MutableSharedFlow<MarkedData>
typealias MarkedDataFlow = SharedFlow<MarkedData>
typealias MutableRawMarketDataFlow = MutableSharedFlow<RawMarketData>
typealias RawMarketDataFlow = Flow<RawMarketData>

fun CoroutineScope.createMarketDataBroker(conf: ConfYAMl): MarketDataBroker {
    val launchPub = with(conf.platforms) {
        coinbase.isOn or binance.isOn or bitstamp.isOn or kraken.isOn or deribit.isOn
    }
    val launchSub = conf.mongodb.isOn or conf.platforms.binance.isOn
    return MarketDataBroker(this, conf.zeromq, launchPub, launchSub)
}


class MarketDataBroker internal constructor(
    private val coroutineScope: CoroutineScope,
    private val zmqConfig: ZeroMQConfig,
    private val launchPub: Boolean,
    private val launchSub: Boolean
) {

    private val inFlow: MutableRawMarketDataFlow = MutableSharedFlow()
    private val outFlow: MutableMarkedDataFlow = MutableSharedFlow()

    fun getFlow(): MarkedDataFlow? =
        if (launchSub) {
            outFlow.asSharedFlow()
        } else {
            logger.warn { "No subscription for market data launched on ${zmqConfig.address}" }
            null
        }

    suspend fun publishFlow(rawMarketDataFlow: RawMarketDataFlow) =
        if (launchPub) {
            rawMarketDataFlow.collect { inFlow.emit(it) }
        } else {
            logger.warn { "No market data publication server launched on ${zmqConfig.address}" }
        }

    fun launchBroker(): Job =
        coroutineScope.launch(Dispatchers.IO) {

            val logger = KotlinLogging.logger { }

            ZContext().use { context ->

                if (launchPub) {
                    val pubSocket = context.getPubSocket(zmqConfig, logger)
                    inFlow.onEach { rawData ->
                        pubSocket.send(rawData)
                    }.launchIn(this)
                }

                if (launchSub) {
                    val subSocket = context.getSubSocket(zmqConfig, logger)
                    for (rawData in subSocket.recvStrStream())
                        try {
                            outFlow.emit(MarketDataParser.parseRawMarketData(rawData))
                        } catch (e: Throwable) {
                            logger.error(e) { "Received corrupted market data" }
                        }
                }
            }
        }

    private val pubInfo = "Publishing market data on ${zmqConfig.address}"
    private val pubError = "Failed to launch market data publication on ${zmqConfig.address}"
    private val subInfo = "Subscribing to market data feed on ${zmqConfig.address}"
    private val subError = "Failed to consume market data from ${zmqConfig.address}"

    private fun ZContext.getPubSocket(zmq: ZeroMQConfig, logger: KLogger): ZMQ.Socket =
        try {
            logger.debug { pubInfo }
            createSocket(SocketType.PUB).apply { bind(zmq.address) }
        } catch (e: Throwable) {
            logger.error(e) { pubError }
            throw RuntimeException(pubError)
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
