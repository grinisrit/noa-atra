package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.*
import kotlinx.serialization.json.*

interface BinanceData : ChannelData {
    val type: String
}

@Serializable
data class PartialBookDepth(
    val lastUpdateId: Long,
    val bids: List<List<String>>,
    val asks: List<List<String>>,
) : BinanceData {
    override val type = "partialBookDepth"
}

@Serializable
data class Trade(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("t") val tradeID: Long,
    @SerialName("p") val price: String,
    @SerialName("q") val quantity: String,
    @SerialName("b") val buyerOrderID: Long,
    @SerialName("a") val sellerOrderID: Long,
    @SerialName("T") val tradeTime: Long,
    @SerialName("m") val isMarketMaker: Boolean,
    @SerialName("M") val ignore: Boolean,
) : BinanceData {
    override val type = "trade"
}

// TODO()
object BinanceDataSerializer : JsonContentPolymorphicSerializer<BinanceData>(BinanceData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "lastUpdateId" in element.jsonObject -> PartialBookDepth.serializer()
        else -> Trade.serializer()
    }
}