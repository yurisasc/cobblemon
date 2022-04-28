package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.net.messages.common.pokemon.update.evolution.EvolutionLikeUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledEvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.evolution.DummyEvolution
import net.minecraft.network.FriendlyByteBuf

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
        return CobbledEvolutionDisplay(this.current.id, result.species, result.form)
    }

    final override fun encodeSending(buffer: FriendlyByteBuf) {
        buffer.writeUtf(this.sending.id)
        buffer.writeUtf(this.sending.species.name)
        buffer.writeUtf(this.sending.form.name)
    }

    final override fun decodeSending(buffer: FriendlyByteBuf) {
        val id = buffer.readUtf()
        val speciesName = buffer.readUtf()
        val formName = buffer.readUtf()
        val species = PokemonSpecies.getByName(speciesName) ?: throw IllegalArgumentException("Cannot resolve species from name $speciesName")
        val form = species.forms.firstOrNull { form -> form.name.equals(formName, true) } ?: throw IllegalArgumentException("Cannot resolve form for ${species.name} from ID $formName")
        this.sending = CobbledEvolutionDisplay(id, species, form)
    }

}