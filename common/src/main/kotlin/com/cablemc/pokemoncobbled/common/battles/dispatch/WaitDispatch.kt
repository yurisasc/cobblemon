package com.cablemc.pokemoncobbled.common.battles.dispatch

class WaitDispatch(delaySeconds: Float) : DispatchResult {
    val readyTime = System.currentTimeMillis() + (delaySeconds * 1000).toInt()
    override fun canProceed() = System.currentTimeMillis() >= readyTime
}