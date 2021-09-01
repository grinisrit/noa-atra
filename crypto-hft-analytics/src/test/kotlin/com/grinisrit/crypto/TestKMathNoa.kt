package com.grinisrit.crypto

import kotlin.test.Test
import space.kscience.kmath.noa.cudaAvailable

class TestKMathNoa {

    @Test
    fun checkCuda() {
        println("CUDA found: ${cudaAvailable()}")
    }

}