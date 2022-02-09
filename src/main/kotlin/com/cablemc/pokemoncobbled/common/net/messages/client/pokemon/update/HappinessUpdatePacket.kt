package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.event.pokemon.HappinessUpdateEvent
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.EVENT_BUS
import net.minecraftforge.common.MinecraftForge

class HappinessUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_BYTE
    override fun set(pokemon: Pokemon, value: Int) {
        if(pokemon.isPlayerOwned()) EVENT_BUS.post(pokemon.getOwner()?.let { HappinessUpdateEvent(it, pokemon, pokemon.happiness) })
        pokemon.happiness = value
   }
}