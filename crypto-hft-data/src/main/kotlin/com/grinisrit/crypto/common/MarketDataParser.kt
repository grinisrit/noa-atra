package com.grinisrit.crypto.common

import com.grinisrit.crypto.binance.BinanceDataSerializer
import com.grinisrit.crypto.bitstamp.BitstampDataSerializer
import com.grinisrit.crypto.coinbase.CoinbaseDataSerializer
import com.grinisrit.crypto.common.models.PlatformData
import com.grinisrit.crypto.common.models.TimestampedData
import com.grinisrit.crypto.deribit.DeribitDataSerializer
import com.grinisrit.crypto.finery.FineryDataSerializer
import com.grinisrit.crypto.kraken.KrakenDataSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.time.Instant

typealias RawMarketJson = String
typealias TimestampedMarketData = TimestampedData<PlatformData>

enum class PlatformName {
    binance,
    bitstamp,
    coinbase,
    deribit,
    kraken,
    finery
    ;
}

object MarketDataParser {

    private const val internalDelimiter = "/**/"

    private val decoder = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun dataStringOf(platformName: String, receivingDateTime: Instant, dataJSON: String): String {
        return buildString {
            append(platformName)
            append(internalDelimiter)
            append(receivingDateTime.toString())
            append(internalDelimiter)
            append(dataJSON)
        }
    }

    fun getRawPlatformName(rawData: RawMarketJson): String {
        return rawData.split(internalDelimiter).first()
    }

    fun getPlatformName(rawData: RawMarketJson): PlatformName {
        return PlatformName.valueOf(getRawPlatformName(rawData))
    }

    fun <T> decodeJsonData(
        jsonData: String,
        serializer: KSerializer<T>
    ) = decoder.decodeFromString(serializer, jsonData)

    fun <T : PlatformData> fromDataString(
        dataString: String,
        serializer: KSerializer<T>
    ): TimestampedData<T> {
        val (_, receivingDateTimeString, jsonData) = dataString.split(internalDelimiter)
        return TimestampedData(
            Instant.parse(receivingDateTimeString),
            decodeJsonData(jsonData, serializer)
        )
    }

    fun parseRawMarketData(rawData: RawMarketJson): TimestampedMarketData =
        when (getPlatformName(rawData)) {
            PlatformName.binance -> fromDataString(rawData, BinanceDataSerializer)
            PlatformName.coinbase -> fromDataString(rawData, CoinbaseDataSerializer)
            PlatformName.bitstamp -> fromDataString(rawData, BitstampDataSerializer)
            PlatformName.kraken -> fromDataString(rawData, KrakenDataSerializer)
            PlatformName.deribit -> fromDataString(rawData, DeribitDataSerializer)
            PlatformName.finery -> fromDataString(rawData, FineryDataSerializer)
        }


}