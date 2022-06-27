package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.common.pokemon.update.evolution.EvolutionLikeUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledEvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.evolution.variants.DummyEvolution
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
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
            CobbledEvents.EVOLUTION_DISPLAY.post(event)
            return event.display
        }

        internal fun encodeSending(display: EvolutionDisplay, buffer: PacketByteBuf) {
            buffer.writeString(display.id)
            buffer.writeString(display.species.name)
            buffer.writeSizedInt(IntSize.U_BYTE, display.aspects.size)
            display.aspects.forEach { aspect ->
                buffer.writeString(aspect)
            }
        }

        internal fun decodeSending(buffer: PacketByteBuf): EvolutionDisplay {
            val id = buffer.readString()
            val speciesName = buffer.readString()
            val species = PokemonSpecies.getByName(speciesName) ?: throw IllegalArgumentException("Cannot resolve species from name $speciesName")
            val aspects = mutableSetOf<String>()
            repeat(buffer.readSizedInt(IntSize.U_BYTE)) {
                aspects += buffer.readString()
            }
            return CobbledEvolutionDisplay(id, species, aspects)
        }

    }

}