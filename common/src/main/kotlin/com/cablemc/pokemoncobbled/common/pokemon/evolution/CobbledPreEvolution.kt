package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.cablemc.pokemoncobbled.common.pokemon.Species
import net.minecraft.util.Identifier

class CobbledPreEvolution(
    private val speciesName: Identifier,
    private val formName: String? = null,
) : PreEvolution {

    override val species: Species
        get() = PokemonSpecies.getByIdentifier(this.speciesName) ?: throw IllegalArgumentException("Cannot find species with $speciesName")

    override val form: FormData
        get() =
            if (this.formName.isNullOrBlank())
                this.species.forms.firstOrNull() ?: species.standardForm
            else
                this.species.forms.firstOrNull { form -> form.name.equals(this.formName, true) } ?: throw IllegalArgumentException("Cannot find form with name $formName")

}
