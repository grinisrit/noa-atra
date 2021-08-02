package com.grinisrit.crypto

import kotlin.test.Test
import space.kscience.kmath.noa.cudaAvailable

class TestKmathNoa {

    @Test
    fun checkCuda() {
        println("CUDA found: ${cudaAvailable()}")
    }


}