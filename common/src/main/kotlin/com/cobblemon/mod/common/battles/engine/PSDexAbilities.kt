package com.cobblemon.mod.common.battles.engine

interface AbilityEventMethods {
    val onCheckShow: (PSBattle.(pokemon: PSPokemon) -> Unit)? get() = null
    val onEnd: (PSBattle.(target: Triple<PSPokemon, PSSide, PSField>) -> Unit)? get() = null // target was actually meant to be a union type of those 3
    val onPreStart: (PSBattle.(pokemon: PSPokemon) -> Unit)? get() = null
    val onStart: (PSBattle.(target: PSPokemon) -> Unit)? get() = null
}