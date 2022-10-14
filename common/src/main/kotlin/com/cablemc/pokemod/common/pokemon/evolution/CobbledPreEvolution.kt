/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution

import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.pokemon.FormData
import com.cablemc.pokemod.common.pokemon.Species
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
