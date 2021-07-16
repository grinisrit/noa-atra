package com.grinisrit.crypto


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grinisrit.crypto.binance.BinanceDataSerializer
import com.grinisrit.crypto.binance.Trade
import com.grinisrit.crypto.coinbase.Ticker
import com.grinisrit.crypto.common.DataTransport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.html.h1
import org.zeromq.SocketType
import org.zeromq.ZContext
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.Plotly
import space.kscience.plotly.models.*
import space.kscience.plotly.plot
import space.kscience.plotly.server.close
import space.kscience.plotly.server.pushUpdates
import space.kscience.plotly.server.serve
import space.kscience.plotly.server.show
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue

fun getMessage() = flow {
    val context = ZContext()
    val socketSUB = context.createSocket(SocketType.SUB)
    socketSUB.connect("tcp://localhost:5899")
    socketSUB.subscribe("")
    while (true) {
        val data = socketSUB.recvStr()
        emit(data)
    }
}


fun getNumbers() = flow {
    getMessage().collect {
        val dataTime = DataTransport.fromDataString(it, BinanceDataSerializer)
        with(dataTime.data) {
            if (this is Trade && (this as Trade).symbol == "BTCUSDT"){
                emit((this as Trade).price.toDouble())
            }
        }
    }

}


fun main() {

    val sinTrace = Trace() {
        name = "BTC"
        line.dash = Dash.dot
    }


    val server = Plotly.serve(port = 3872) {
        embedData = true

        page { plotly ->
            h1 { +"Bitcoin rate" }
            plot(renderer = plotly) {
                traces(sinTrace)
                layout {
                    title = "По чём биткоин"
                    xaxis.title = "Времени нету но вы держитесь"
                    yaxis.title = "Баксы"
                }
            }
        }

        pushUpdates(50)       // start sending updates via websocket to the front-end
    }

    server.show()


    val y = CopyOnWriteArrayList<Double>()

    val yQueue = LinkedBlockingQueue<Double>()


    GlobalScope.launch {
        while (isActive) {
            sinTrace.y.numbers = y.takeLast(100)
        }
    }


    GlobalScope.launch {
        getNumbers().collect {
            y.add(it)
        }
    }

    println("Press Enter to close server")
    while (readLine()?.trim() != "exit") {
        //wait
    }

    server.close()
}