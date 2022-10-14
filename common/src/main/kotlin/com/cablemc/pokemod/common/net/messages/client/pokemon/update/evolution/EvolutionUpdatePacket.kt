/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemod.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.net.messages.client.storage.EvolutionLikeUpdatePacket
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.evolution.CobbledEvolutionDisplay
import com.cablemc.pokemod.common.pokemon.evolution.variants.DummyEvolution
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * The base for all [Evolution] updates.
 *
 * @author Licious
 * @since April 28th, 2022.
 */
abstract class EvolutionUpdatePacket : EvolutionLikeUpdatePacket<Evolution, EvolutionDisplay>() {

    override var current: Evolution = DummyEvolution()
    override var sending: EvolutionDisplay = CobbledEvolutionDisplay("dummy", Pokemon())

    override fun createSending(pokemon: Pokemon): EvolutionDisplay {
        return Companion.createSending(pokemon, this.current)
    }

    final override fun encodeSending(buffer: PacketByteBuf) {
        Companion.encodeSending(this.sending, buffer)
    }

    final override fun decodeSending(buffer: PacketByteBuf) {
        this.sending = Companion.decodeSending(buffer)
    }

    companion object {

        // Hacks for DRY, see CobbledServerEvolutionController for context

        internal fun createSending(pokemon: Pokemon, evolution: Evolution): EvolutionDisplay {
            val result = Pokemon().apply {
                species = pokemon.species
                shiny = pokemon.shiny
                form = pokemon.form
                gender = pokemon.gender
            }
            evolution.result.apply(result)
            val expectedDisplay = CobbledEvolutionDisplay(evolution.id, result)
            val event = EvolutionDisplayEvent(result, expectedDisplay, evolution)
            PokemodEvents.EVOLUTION_DISPLAY.post(event)
            return event.display
        }

        internal fun encodeSending(display: EvolutionDisplay, buffer: PacketByteBuf) {
            buffer.writeString(display.id)
            buffer.writeIdentifier(display.species.resourceIdentifier)
            buffer.writeSizedInt(IntSize.U_BYTE, display.aspects.size)
            display.aspects.forEach { aspect ->
                buffer.writeString(aspect)
            }
        }

        internal fun decodeSending(buffer: PacketByteBuf): EvolutionDisplay {
            val id = buffer.readString()
            val speciesIdentifier = buffer.readIdentifier()
            val species = PokemonSpecies.getByIdentifier(speciesIdentifier) ?: throw IllegalArgumentException("Cannot resolve species from $speciesIdentifier")
            val aspects = mutableSetOf<String>()
            repeat(buffer.readSizedInt(IntSize.U_BYTE)) {
                aspects += buffer.readString()
            }
            return CobbledEvolutionDisplay(id, species, aspects)
        }

    }

}