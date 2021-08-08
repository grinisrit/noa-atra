package com.grinisrit.crypto

import space.kscience.dataforge.meta.invoke
import space.kscience.dataforge.meta.set
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile

import space.kscience.plotly.models.Bar
import space.kscience.plotly.models.BarMode
import space.kscience.plotly.palettes.Xkcd


/**
 * - Grouped bar chart
 * - Use XKCD color palette
 */
fun main() {
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
}