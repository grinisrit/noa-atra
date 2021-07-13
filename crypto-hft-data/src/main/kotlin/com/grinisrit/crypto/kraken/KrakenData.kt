package com.grinisrit.crypto.kraken

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.grinisrit.crypto.common.ChannelData
import com.grinisrit.crypto.common.CustomJsonParser

interface KrakenData : ChannelData {
    val channelName: String
}

class Event : KrakenData {
    override val channelName: String = "event"
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
) : KrakenData

data class OrderData(
    val price: String,
    val volume: String,
    val timestamp: String,
)

data class BookData(
    val asks: List<OrderData>,
    val bids: List<OrderData>,
)

data class Book(
    val channelId: Int,
    val bookData: BookData,
    override val channelName: String,
    val pair: String,
) : KrakenData

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

    private fun parseBook(dataArray: JsonArray): Book =
        Book(
            dataArray[0].asInt,
            BookData(
                dataArray[1].asJsonObject["as"].asJsonArray.map { parseOrderData(it.asJsonArray) },
                dataArray[1].asJsonObject["as"].asJsonArray.map { parseOrderData(it.asJsonArray) }
            ),
            dataArray[2].asString,
            dataArray[3].asString,
        )

    override fun parse(jsonString: String): KrakenData {
        val obj = Gson().fromJson(jsonString, JsonElement::class.java)
        if (obj.isJsonObject) {
            return Event()
        }
        val dataArray = obj.asJsonArray
        val channelName = dataArray[dataArray.size() - 2]
        return if (channelName.asString== "trade") {
            parseTrade(dataArray)
        } else if (channelName.asString.startsWith("\"book\"")) {
            parseBook(dataArray)
        } else {
            //TODO()
            Event()
        }
    }
}



