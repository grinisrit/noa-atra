package com.grinisrit.crypto

import com.grinisrit.crypto.common.PlatformName

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Platform {

    @SerialName("websocket_address")
    val websocketAddress: String
    val status: String
    val symbols: List<String>
    val platformName: PlatformName

    val name: String
        get() = platformName.toString()

    val isOn: Boolean
        get() = status == "on"
}

@Serializable
data class CoinbasePlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.coinbase
}

@Serializable
data class BinancePlatform(
    @SerialName("api_address")
    val apiAddress: String,
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.binance
}

@Serializable
data class DeribitPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>
) : Platform {
    override val platformName: PlatformName = PlatformName.deribit
}

@Serializable
data class KrakenPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.kraken
}

@Serializable
data class BitstampPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.bitstamp
}


@Serializable
data class FineryPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
    @SerialName("key")
    val key: String,
    @SerialName("secret")
    val secret: String,
) : Platform {
    override val platformName: PlatformName = PlatformName.finery
}

@Serializable
data class Platforms(
    val coinbase: CoinbasePlatform,
    val kraken: KrakenPlatform,
    val binance: BinancePlatform,
    val deribit: DeribitPlatform,
    val bitstamp: BitstampPlatform,
    val finery: FineryPlatform,
)