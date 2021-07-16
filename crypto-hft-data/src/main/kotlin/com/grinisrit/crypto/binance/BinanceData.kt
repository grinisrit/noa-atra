package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.*
import kotlinx.serialization.json.*

interface BinanceData : ChannelData {
    val type: String
}

@Serializable
data class OrderData(
    val price: String,
    val amount: String,
)

object OrderDataSerializer :
    JsonTransformingSerializer<OrderData>(OrderData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("amount", it[1])
            }
        }
    }
}

@Serializable
data class Snapshot(
    val lastUpdateId: Long,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
) : BinanceData {
    override val type = "snapshot"
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

@Serializable
data class BookUpdate(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("U") val firstUpdateId: Long,
    @SerialName("u") val finalUpdateId: Long,
    @SerialName("b") val bids: List<List<String>>,
    @SerialName("a") val asks: List<List<String>>,
) : BinanceData {
    override val type = "update"
}

@Serializable
data class Event(
    override val type: String = "event"
) : BinanceData

// TODO()
object BinanceDataSerializer : JsonContentPolymorphicSerializer<BinanceData>(BinanceData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "lastUpdateId" in element.jsonObject -> Snapshot.serializer()
        "U" in element.jsonObject -> BookUpdate.serializer()
        "t" in element.jsonObject -> Trade.serializer()
        else -> Event.serializer()
    }
}