package com.cobblemon.mod.common.trading

import com.cobblemon.mod.common.pokemon.Pokemon

class TradeOffer {
    var pokemon: Pokemon? = null
    var accepted = false

    fun updateOffer(pokemon: Pokemon) {
        this.pokemon = pokemon
        accepted = false
    }
}