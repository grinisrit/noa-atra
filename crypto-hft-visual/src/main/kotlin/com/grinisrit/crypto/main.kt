package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.instantOfEpochMicro
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import space.kscience.dataforge.meta.invoke
import space.kscience.dataforge.meta.set
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile

import space.kscience.plotly.models.Bar
import space.kscience.plotly.models.BarMode
import space.kscience.plotly.palettes.Xkcd
import java.time.Instant


suspend fun main() {


    println(Instant.ofEpochMilli(1628228172009))

}