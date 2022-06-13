package com.cablemc.pokemoncobbled.common.battles.dispatch

fun interface DispatchResult {
    fun canProceed(): Boolean
}

val GO = DispatchResult { true }