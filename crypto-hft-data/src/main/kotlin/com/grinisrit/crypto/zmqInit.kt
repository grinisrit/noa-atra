package com.grinisrit.crypto.common

import com.grinisrit.crypto.ZeroMQ
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

fun ZContext.getPubSocket(zmq: ZeroMQ): ZMQ.Socket {
    return createSocket(SocketType.PUB).apply { bind(zmq.address) }
}

fun ZContext.getSubSocket(zmq: ZeroMQ, topic: String = ""): ZMQ.Socket {
    return createSocket(SocketType.SUB).apply {
        connect(zmq.address)
        subscribe(topic)
    }
}