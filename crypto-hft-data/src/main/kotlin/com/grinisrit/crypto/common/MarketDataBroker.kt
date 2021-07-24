package com.grinisrit.crypto.common

import com.grinisrit.crypto.ZeroMQConfig
import com.grinisrit.crypto.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.lang.RuntimeException

typealias MutableMarkedDataFlow = MutableSharedFlow<MarkedData>
typealias MarkedDataFlow = SharedFlow<MarkedData>
typealias MutableRawMarketDataFlow = MutableSharedFlow<RawMarketData>
typealias RawMarketDataFlow = Flow<RawMarketData>

fun CoroutineScope.createMarketDataBroker(zmqConfig: ZeroMQConfig): MarketDataBroker =
    MarketDataBroker(this, zmqConfig)

class MarketDataBroker internal constructor(
    private val coroutineScope: CoroutineScope,
    private val zmqConfig: ZeroMQConfig
) {

    private val inFlow: MutableRawMarketDataFlow = MutableSharedFlow()
    private val outFlow: MutableMarkedDataFlow = MutableSharedFlow()


    fun getFlow(): MarkedDataFlow = outFlow.asSharedFlow()

    suspend fun publishFlow(rawMarketDataFlow: RawMarketDataFlow) =
        rawMarketDataFlow.collect { inFlow.emit(it) }

    fun launchBroker(): Job =
        coroutineScope.launch(Dispatchers.IO) {
            ZContext().use { context ->

                val pubSocket = context.getPubSocket(zmqConfig)
                inFlow.onEach { rawData ->
                    pubSocket.send(rawData)
                }.launchIn(this)


                val subSocket = context.getSubSocket(zmqConfig)
                for (rawData in subSocket.recvStrStream())
                    try {
                        outFlow.emit(MarketDataParser.parseRawMarketData(rawData))
                    } catch (e: Throwable) {
                        logger.error(e) { "Received corrupted market data " }
                    }

            }
        }

    private val pubInfo = "Publishing market data on ${zmqConfig.address}"
    private val pubError = "Failed to launch market data publication on ${zmqConfig.address}"
    private val subError = "Failed to consume market data from ${zmqConfig.address}"

    private fun ZContext.getPubSocket(zmq: ZeroMQConfig): ZMQ.Socket =
        try {
            logger.debug { pubInfo }
            createSocket(SocketType.PUB).apply { bind(zmq.address) }
        } catch (e: Throwable) {
            logger.error(e) { pubError }
            throw RuntimeException(pubError)
        }

    private fun ZContext.getSubSocket(zmq: ZeroMQConfig): ZMQ.Socket =
        try {
            createSocket(SocketType.SUB).apply {
                connect(zmq.address)
                subscribe("")
            }
        } catch (e: Throwable) {
            logger.error(e) { subError }
            throw RuntimeException(subError)
        }
}
