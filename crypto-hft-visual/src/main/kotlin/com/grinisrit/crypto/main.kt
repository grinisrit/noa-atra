package com.grinisrit.crypto

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

fun f() = flow {
    kotlinx.coroutines.delay(1000L)
    emit(10)
}

/**
 * - Grouped bar chart
 * - Use XKCD color palette
 */
suspend fun main() {

    println(f().toList().asSequence().iterator().next())

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