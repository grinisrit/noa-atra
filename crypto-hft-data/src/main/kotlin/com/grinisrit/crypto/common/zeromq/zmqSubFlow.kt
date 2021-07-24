package com.grinisrit.crypto.common.zeromq

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.zeromq.ZMQ
import kotlin.coroutines.CoroutineContext

fun zmqSubFlow(subSocket: ZMQ.Socket, context: CoroutineContext = Dispatchers.IO): SharedFlow<String> {
    val commonDataFlow = MutableSharedFlow<String>(1000)
    CoroutineScope(context).launch {
        for (message in subSocket.recvStrStream()) {
            commonDataFlow.emit(message)
        }
    }
    return commonDataFlow
}