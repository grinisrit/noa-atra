package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.UnbookedEvent
import com.grinisrit.crypto.common.models.PlatformData
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.Instant

interface BitstampData : PlatformData

interface BitstampDataTime : BitstampData {
    val datetime: Instant
}

@Serializable
data class OrderData(
    val price: Float,
    val amount: Float,
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
data class BitstampOrderBook(
    val data: OrderBookData,
    val channel: String,
    val event: String,
) : BitstampDataTime {
    override val type = BitstampDataType.order_book

    override val datetime: Instant
        get() = Instant.ofEpochMilli(data.microtimestamp / 1000)
}

@Serializable
data class TradeData(
    val amount: Float,
    val amount_str: String,
    val price: Float,
    val price_str: String,
    val buy_order_id: Long,
    val id: Long,
    val microtimestamp: Long,
    val timestamp: Long,
    val sell_order_id: Long,
    val type: Int,
)

@Serializable
data class BitstampTrade(
    val channel: String,
    val data: TradeData,
    val event: String,
) : BitstampDataTime {
    override val type = BitstampDataType.trade

    override val datetime: Instant
        get() = Instant.ofEpochMilli(data.microtimestamp / 1000)
}

@Serializable
class BitstampEvent: BitstampData, UnbookedEvent


object BitstampDataSerializer : JsonContentPolymorphicSerializer<BitstampData>(BitstampData::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["event"]!!.jsonPrimitive.content) {
            "trade" -> BitstampTrade.serializer()
            "data" -> BitstampOrderBook.serializer()
            else -> BitstampEvent.serializer()
        }
}
