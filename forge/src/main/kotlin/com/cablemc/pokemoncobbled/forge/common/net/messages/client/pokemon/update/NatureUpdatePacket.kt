package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.forge.mod.PokemonCobbledMod

class NatureUpdatePacket(
    private var mintNature : Boolean = false
) : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String, mintNature: Boolean): this() {
        this.setTarget(pokemon)
        this.value = value
        this.mintNature = mintNature
    }

    override fun set(pokemon: Pokemon, value: String) {
        // Check for removing mint
        if (mintNature && value.isEmpty()) {
            pokemon.mintedNature = null
            return
        }

        val nature = Natures.getNature(cobbledResource(value))
        // Validate the nature locally
        if (nature == null) {
            PokemonCobbledMod.LOGGER.warn("A invalid nature of '$value' was attempted to be put onto: '$pokemon'")
            return
        }

        // Check which nature to modify
        if (!mintNature) {
            pokemon.nature = nature
        } else {
            pokemon.mintedNature = nature
        }
    }
}