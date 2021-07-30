package com.grinisrit.crypto

/*
import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.common.MarketDataParser
import com.grinisrit.crypto.common.zeromq.ZeroMQSubClient
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import kotlinx.coroutines.*
import kotlinx.html.h1
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.Plotly
import space.kscience.plotly.models.*
import space.kscience.plotly.plot
import space.kscience.plotly.server.close
import space.kscience.plotly.server.pushUpdates
import space.kscience.plotly.server.serve
import space.kscience.plotly.server.show
import java.io.File
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>) {

    val cliParser = ArgParser("visual")

    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()

    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"

    val config = parseConf(File(configPath).readText())

    val subSocket = getSubSocket(config.zeromq, "binance")

    val zeroMQSubClient = ZeroMQSubClient(subSocket)

    GlobalScope.launch {
        zeroMQSubClient.run()
    }

    val sinTrace = Trace {
        name = "BTC to USDT"
        line.dash = Dash.dot
    }


    val server = Plotly.serve(port = 3872) {
        embedData = true

        page { plotly ->
            h1 { +"Bitcoin rate" }
            plot(renderer = plotly) {
                traces(sinTrace)
                layout {
                    title = "BTC-USDT live trades"
                    xaxis.title = "Time, UTC"
                    yaxis.title = "Price, USDT"
                }
            }
        }

        pushUpdates(50)       // start sending updates via websocket to the front-end
    }

    server.show()


    val data = CopyOnWriteArrayList<Pair<Instant, Float>>()

    GlobalScope.launch {
        println(Thread.currentThread())
        while (isActive) {
            sinTrace {
                x.set(data.takeLast(1000).map { it.first.toString() })
            }
            sinTrace.y.numbers = data.takeLast(1000).map { it.second }
        }
    }


    GlobalScope.launch {
        println(Thread.currentThread())
        zeroMQSubClient.getData("binance").collect {
            val dataTime = MarketDataParser.fromDataString(it, BinanceDataSerializer)
            with(dataTime.platform_data) {
                if (this is Trade && symbol == "BTCUSDT"){
                    data.add(Pair(Instant.ofEpochMilli(tradeTime), price))
                }
            }
        }
    }

    println("Press Enter to close server")
    while (readLine()?.trim() != "exit") {
        //wait
    }

    server.close()
}

 */