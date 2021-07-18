package com.grinisrit.crypto.common

import com.grinisrit.crypto.ZeroMQ
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

fun getPubSocket(zmq: ZeroMQ): ZMQ.Socket{
    return ZContext().createSocket(SocketType.PUB).apply { bind(zmq.address) }
}

fun getSubSocket(zmq: ZeroMQ): ZMQ.Socket{
    return ZContext().createSocket(SocketType.SUB).apply { connect(zmq.address) }
}