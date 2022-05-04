package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.evolution.EvolutionDisplayEvent
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.net.messages.common.pokemon.update.evolution.EvolutionLikeUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledEvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.evolution.DummyEvolution
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
        val result = pokemon.clone(useJSON = false)
        this.current.result.apply(result)
        val event = EvolutionDisplayEvent(result, this.current)
        CobbledEvents.EVOLUTION_DISPLAY.post(event)
        return CobbledEvolutionDisplay(this.current.id, event.pokemon.species, event.pokemon.form)
    }

    final override fun encodeSending(buffer: PacketByteBuf) {
        buffer.writeString(this.sending.id)
        buffer.writeString(this.sending.species.name)
        buffer.writeString(this.sending.form.name)
    }

    final override fun decodeSending(buffer: PacketByteBuf) {
        val id = buffer.readString()
        val speciesName = buffer.readString()
        val formName = buffer.readString()
        val species = PokemonSpecies.getByName(speciesName) ?: throw IllegalArgumentException("Cannot resolve species from name $speciesName")
        val form = species.forms.firstOrNull { form -> form.name.equals(formName, true) } ?: throw IllegalArgumentException("Cannot resolve form for ${species.name} from ID $formName")
        this.sending = CobbledEvolutionDisplay(id, species, form)
    }

}