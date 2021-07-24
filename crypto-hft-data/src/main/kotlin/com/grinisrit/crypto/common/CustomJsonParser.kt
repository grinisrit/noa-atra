package com.grinisrit.crypto.common

interface CustomJsonParser<T: PlatformData> {
    fun parse(jsonString: String): T
}