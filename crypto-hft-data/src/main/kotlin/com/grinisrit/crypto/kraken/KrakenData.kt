package com.grinisrit.crypto.kraken

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.grinisrit.crypto.common.ChannelData
import com.grinisrit.crypto.common.CustomJsonParser

interface KrakenData : ChannelData {
    val channelName: String
    val type: String
}

class Event : KrakenData {
    override val channelName: String = "event"
    override val type: String = "event"
}

data class TradeData(
    val price: String,
    val volume: String,
    val time: String,
    val side: String,
    val orderType: String,
    val misc: String,
)

data class Trade(
    val channelId: Int,
    val tradeData: List<TradeData>,
    override val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "trade"
}

data class OrderData(
    val price: String,
    val volume: String,
    val timestamp: String,
)

data class BookSnapshotData(
    val asks: List<OrderData>,
    val bids: List<OrderData>,
)

data class BookSnapshot(
    val channelId: Int,
    val bookData: BookSnapshotData,
    override val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "snapshot"
}

data class UpdateData(
    val price: String,
    val volume: String,
    val timestamp: String,
    val updateType: String?,
)

data class UpdateAction(
    val updateData: List<UpdateData>,
    val bookChecksum: String?,
)

data class BookUpdate(
    val channelId: Int,
    val askUpdate: UpdateAction?,
    val bidUpdate: UpdateAction?,
    override val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "update"
}

class KrakenJsonParser : CustomJsonParser<KrakenData> {
    private fun parseTradeData(dataArray: JsonArray): TradeData =
        TradeData(
            dataArray[0].asString,
            dataArray[1].asString,
            dataArray[2].asString,
            dataArray[3].asString,
            dataArray[4].asString,
            dataArray[5].asString,
        )

    private fun parseTrade(dataArray: JsonArray): Trade =
        Trade(
            dataArray[0].asInt,
            dataArray[1].asJsonArray.map { parseTradeData(it.asJsonArray) },
            dataArray[2].asString,
            dataArray[3].asString,
        )

    private fun parseOrderData(dataArray: JsonArray): OrderData =
        OrderData(
            dataArray[0].asString,
            dataArray[1].asString,
            dataArray[2].asString,
        )

    private fun parseBookSnapshot(dataArray: JsonArray): BookSnapshot =
        BookSnapshot(
            dataArray[0].asInt,
            BookSnapshotData(
                dataArray[1].asJsonObject["as"].asJsonArray.map { parseOrderData(it.asJsonArray) },
                dataArray[1].asJsonObject["as"].asJsonArray.map { parseOrderData(it.asJsonArray) }
            ),
            dataArray[2].asString,
            dataArray[3].asString,
        )

    private fun parseUpdateData(dataArray: JsonArray): UpdateData =
        UpdateData(
            dataArray[0].asString,
            dataArray[1].asString,
            dataArray[2].asString,
            (if (dataArray.size() > 3) {
                dataArray[3].asString
            } else {
                null
            }),
        )

    private fun parseUpdateAction(dataObject: JsonObject, symbol: String): UpdateAction? =
        if (symbol in dataObject.keySet()) {
            UpdateAction(
                dataObject[symbol].asJsonArray.map { parseUpdateData(it.asJsonArray) },
                (if ("c" in  dataObject.keySet()) {
                    dataObject["c"].asString
                } else {
                    null
                })
            )
        } else {
            null
        }

    private fun parseBookUpdate(dataArray: JsonArray): BookUpdate {

        val firstUpdateAction = dataArray[1].asJsonObject
        val secondUpdateAction = if (dataArray.size() > 4) {
            dataArray[2].asJsonObject
        } else {
            null
        }

        val a = parseUpdateAction(firstUpdateAction, "a")
        val b = if (a == null) {
            parseUpdateAction(firstUpdateAction, "b")
        } else if (secondUpdateAction!= null) {
            parseUpdateAction(secondUpdateAction, "b")
        } else {
            null
        }

        return BookUpdate(
            dataArray[0].asInt,
            a,
            b,
            dataArray[dataArray.size() - 2].asString,
            dataArray.last().asString,
        )
    }

    override fun parse(jsonString: String): KrakenData {
        val obj = Gson().fromJson(jsonString, JsonElement::class.java)
        if (obj.isJsonObject) {
            return Event()
        }
        val dataArray = obj.asJsonArray
        val channelName = dataArray[dataArray.size() - 2]
        return if (channelName.asString == "trade") {
            parseTrade(dataArray)
        } else if (channelName.asString.startsWith("book")) {
            val dataObject = dataArray[1].asJsonObject
            if ("as" in dataObject.keySet()) {
                parseBookSnapshot(dataArray)
            } else {
                parseBookUpdate(dataArray)
            }
        } else {
            //TODO()
            Event()
        }
    }
}



