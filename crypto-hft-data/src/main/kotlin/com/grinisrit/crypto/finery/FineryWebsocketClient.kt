package com.grinisrit.crypto.finery

import com.grinisrit.crypto.FineryPlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.io.File


fun FineryPlatform.createFinerySource(request: String): FineryWebsocketClient {
    return FineryWebsocketClient(this, request)
}

class FineryWebsocketClient
internal constructor(
    platform: FineryPlatform,
    request: String,
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 4000L,
    aliveBound = 1000,
) {
    private val key = File(platform.keyPath).readText()
    private val secret = File(platform.secretPath).readText()

    override suspend fun DefaultClientWebSocketSession.authorize() {
        val authRequest = getAuthRequest(key, secret)
        println(authRequest)
        send(Frame.Text(authRequest))
        delay(1000L)
    }

}