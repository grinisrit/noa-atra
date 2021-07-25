package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.UnbookedEvent
import com.grinisrit.crypto.common.PlatformData
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.Instant

interface BinanceData : PlatformData

interface BinanceDataTime : BinanceData {
    val datetime: Instant
}

@Serializable
data class OrderData(
    val price: Double,
    val amount: Double,
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
data class SnapshotData(
    val lastUpdateId: Long,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
)

@Serializable
data class Snapshot(
    val snapshot: SnapshotData,
    val symbol: String,
) : BinanceData {
    override val type = "snapshot"
}

@Serializable
data class Trade(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("t") val tradeID: Long,
    @SerialName("p") val price: Double,
    @SerialName("q") val quantity: Double,
    @SerialName("b") val buyerOrderID: Long,
    @SerialName("a") val sellerOrderID: Long,
    @SerialName("T") val tradeTime: Long,
    @SerialName("m") val isMarketMaker: Boolean,
    @SerialName("M") val ignore: Boolean,
) : BinanceDataTime {
    override val type = "trade"

    override val datetime: Instant
        get() = Instant.ofEpochMilli(tradeTime)
}

@Serializable
data class BookUpdate(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("U") val firstUpdateId: Long,
    @SerialName("u") val finalUpdateId: Long,
    @SerialName("b") val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    @SerialName("a") val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
) : BinanceDataTime {
    override val type = "update"

    override val datetime: Instant
        get() = Instant.ofEpochMilli(eventTime)
}

@Serializable
data class Event(
    override val type: String = "event"
) : BinanceData, UnbookedEvent

object BinanceDataSerializer : JsonContentPolymorphicSerializer<BinanceData>(BinanceData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element !is JsonObject -> Event.serializer()
        "snapshot" in element.jsonObject -> Snapshot.serializer()
        "U" in element.jsonObject -> BookUpdate.serializer()
        "t" in element.jsonObject -> Trade.serializer()
        else -> Event.serializer()
    }
}