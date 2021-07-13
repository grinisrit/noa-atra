package com.grinisrit.crypto

import space.kscience.kmath.noa.cudaAvailable


fun main(){
    println("CUDA found: ${cudaAvailable()}")
}