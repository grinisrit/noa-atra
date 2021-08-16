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

fun f() = flow {
    kotlinx.coroutines.delay(1000L)
    emit(10)
}

/**
 * - Grouped bar chart
 * - Use XKCD color palette
 */

suspend fun main() {

    val b = (1628243378.815078 * 1000).toLong()

    println(Instant.ofEpochMilli(b))

    /*
    val trace1 = Bar {
        x.set(listOf(1, 6, 7 ))
        y(20, 14, 23)
        name = "SF Zoo"
        marker {
            color(Xkcd.GREEN)
        }
    }


    val plot = Plotly.plot {
        traces(trace1)

        layout {
            title = "Grouped Bar Chart"
            barmode = BarMode.group
        }
    }
    plot.makeFile()

     */
}