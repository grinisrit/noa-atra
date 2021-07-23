package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.Instant

interface BitstampData : ChannelData {
    val type: String
}

interface BitstampDataTime : BitstampData {
    val datetime: Instant
}

@Serializable
data class OrderData(
    val price: Double,
    val amount: Double,
    val orderId: Long,
)

object OrderDataSerializer :
    JsonTransformingSerializer<OrderData>(OrderData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("amount", it[1])
                put("orderId", it[2])
            }
        }
    }
}

@Serializable
data class OrderBookData(
    val microtimestamp: Long,
    val timestamp: Long,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
)

@Serializable
data class OrderBook(
    val data: OrderBookData,
    val channel: String,
    val event: String,
) : BitstampDataTime {
    override val type = "order_book"

    override val datetime: Instant
        get() = Instant.ofEpochMilli(data.microtimestamp / 1000)
}

@Serializable
data class TradeData(
    val amount: Double,
    val amount_str: String,
    val price: Double,
    val price_str: String,
    val buy_order_id: Long,
    val id: Long,
    val microtimestamp: Long,
    val timestamp: Long,
    val sell_order_id: Long,
    val type: Int,
)

@Serializable
data class Trade(
    val channel: String,
    val data: TradeData,
    val event: String,
) : BitstampDataTime {
    override val type = "trade"

    override val datetime: Instant
        get() = Instant.ofEpochMilli(data.microtimestamp / 1000)
}

@Serializable
data class Event(
    override val type: String = "event"
) : BitstampData


object BitstampDataSerializer : JsonContentPolymorphicSerializer<BitstampData>(BitstampData::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["event"]!!.jsonPrimitive.content) {
            "trade" -> Trade.serializer()
            "data" -> OrderBook.serializer()
            else -> Event.serializer()
        }
}
