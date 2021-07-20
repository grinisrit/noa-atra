package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.binance.BinanceData
import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.sql.Timestamp

interface BitstampData : ChannelData {
    val type: String
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
data class OrderBook(
    val channel: String,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val event: String,
) : BitstampData {
    override val type = "order_book"
}

@Serializable
data class TradeData(
    val amount: Double,
    val amount_str: String,
    val price: Double,
    val price_str: String,
    val buy_order_id: Long,
    val id: Long,
    val microtimestamp: String,
    val timestamp: String,
    val sell_order_id: Long,
    val type: Int,
)

@Serializable
data class Trade(
    val channel: String,
    val data: TradeData,
    val event: String,
) : BitstampData {
    override val type = "trade"
}

@Serializable
data class Event(
    override val type: String = "event"
) : BitstampData


// TODO()
object BitstampDataSerializer : JsonContentPolymorphicSerializer<BitstampData>(BitstampData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
      //  "lastUpdateId" in element.jsonObject -> Snapshot.serializer()
        else -> Event.serializer()
    }
}