package com.grinisrit.crypto.finery

import com.grinisrit.crypto.FineryPlatform
import com.grinisrit.crypto.loadConf
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import java.io.File

suspend fun makeRequest(platform: FineryPlatform) {
     val key = platform.key
     val secret = platform.secret

    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    client.wss(urlString = platform.websocketAddress) {
        val authRequest = getAuthRequest(key, secret)
        send(Frame.Text(authRequest))
        delay(2000L)

        send(Frame.Text("{\"event\": \"bind\", \"feed\": \"I\"}"))

        for (msg in incoming) {
            println((msg as Frame.Text).readText())
        }
    }

}

suspend fun main(args:Array<String>) = coroutineScope {
    val config = loadConf(args)

    makeRequest(config.platforms.finery)
}