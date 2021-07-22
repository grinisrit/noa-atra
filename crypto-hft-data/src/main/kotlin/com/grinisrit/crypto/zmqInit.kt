package com.grinisrit.crypto.common

import com.grinisrit.crypto.ZeroMQ
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

// todo init common context + close
fun getPubSocket(zmq: ZeroMQ): ZMQ.Socket{
    return ZContext().createSocket(SocketType.PUB).apply { bind(zmq.address) }
}

fun getSubSocket(zmq: ZeroMQ, topic: String = ""): ZMQ.Socket{
    return ZContext().createSocket(SocketType.SUB).apply {
        connect(zmq.address)
        subscribe(topic)
    }
}