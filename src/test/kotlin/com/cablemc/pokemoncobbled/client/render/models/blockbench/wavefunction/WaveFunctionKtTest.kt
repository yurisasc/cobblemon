package com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction

import org.junit.jupiter.api.Test

internal class WaveFunctionKtTest {
    @Test
    fun `test extremes of triangle function`() {
        val triWave = triangleFunction(
            amplitude = 2F,
            period = 6F
        )

        for (t in 0 until 100) {
            println(triWave(t / 11.1F))
        }


//        val x1 = 0F
//        val y1 = triWave(x1)
//
//        val x2 = 1F
//        val y2 = triWave(x2)
//
//        val x3 = 2F
//        val y3 = triWave(x3)
//
//        val x4 = 0.5F
//        val y4 = triWave(x4)
//
//        val x5 = 1.5F
//        val y5 = triWave(x5)
//
//        val x6 = 0.25F
//        val y6 = triWave(x6)
//
//        println("$y1, $y2, $y3, $y4, $y5, $y6")
    }
}