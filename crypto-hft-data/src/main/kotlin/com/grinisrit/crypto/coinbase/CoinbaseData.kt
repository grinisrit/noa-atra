package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.models.UnbookedEvent
import com.grinisrit.crypto.common.models.PlatformData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.Instant

interface CoinbaseData : PlatformData

interface CoinbaseDataTime : CoinbaseData {
    val time: String
    val datetime: Instant
        get() = Instant.parse(time)
}

@Serializable
data class Ticker(
    val trade_id: Long,
    val sequence: Long,
    override val time: String,
    val product_id: String,
    val price: Float,
    val side: String,
    val last_size: Float,
    val best_bid: Float,
    val best_ask: Float,
    val open_24h: Float,
    val volume_24h: Float,
    val low_24h: Float,
    val high_24h: Float,
    val volume_30d: Float,
) : CoinbaseDataTime {
    override val type = CoinbaseDataType.ticker
}

@Serializable
data class OrderData(
    val price: Float,
    val amount: Float,
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
data class OrderUpdateData(
    val side: String,
    val price: Float,
    val amount: Float,
)

object OrderUpdateDataSerializer :
    JsonTransformingSerializer<OrderUpdateData>(OrderUpdateData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("side", it[0])
                put("price", it[1])
                put("amount", it[2])
            }
        }
    }
}

@Serializable
data class CoinbaseSnapshot(
    val product_id: String,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
) : CoinbaseData {
    override val type = CoinbaseDataType.snapshot
}

@Serializable
data class CoinbaseL2Update(
    val product_id: String,
    override val time: String,
    val changes: List<@Serializable(with = OrderUpdateDataSerializer::class) OrderUpdateData>,
) : CoinbaseDataTime {
    override val type = CoinbaseDataType.snapshot
}

@Serializable
data class CoinbaseMatch(
    val trade_id: Long,
    val maker_order_id: String,
    val taker_order_id: String,
    val side: String,
    override val time: String,
    val product_id: String,
    val price: Float,
    val size: Float,
    val sequence: Long,
) : CoinbaseDataTime {
    override val type = CoinbaseDataType.match
}

@Serializable
class CoinbaseEvent : CoinbaseData, UnbookedEvent

object CoinbaseDataSerializer : JsonContentPolymorphicSerializer<CoinbaseData>(CoinbaseData::class) {
    override fun selectDeserializer(element: JsonElement) =
        when {
            element !is JsonObject -> CoinbaseEvent.serializer()
            element.jsonObject["type"] !is JsonPrimitive -> CoinbaseEvent.serializer()
            else -> when (element.jsonObject["type"]?.jsonPrimitive?.content) {
                "ticker" -> Ticker.serializer()
                "snapshot" -> CoinbaseSnapshot.serializer()
                "l2update" -> CoinbaseL2Update.serializer()
                "match" -> CoinbaseMatch.serializer()
                else -> CoinbaseEvent.serializer()
            }
        }
}
