package com.grinisrit.crypto.common.zeromq

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import org.zeromq.ZMQ

class ZeroMQSubClient(
    private val subSocket: ZMQ.Socket,
    replay: Int = 1000000
) {
    private val commonDataFlow = MutableSharedFlow<String>(replay)

    fun getData(topic: String) = commonDataFlow.filter { it.startsWith(topic) }

    // TODO Andrei: this function should get topics and context and return the flow
    suspend fun run() {
        for (message in subSocket.recvStrStream()) {
            commonDataFlow.emit(message)
        }
    }
}
