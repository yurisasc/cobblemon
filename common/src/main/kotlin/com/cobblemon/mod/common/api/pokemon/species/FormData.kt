/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier

/**
 * Additional data only present in species that are a variation of an existing species.
 * This is commonly referred to as a Pokémon form.
 * In the context of Cobblemon aspects are used when a form is purely cosmetic.
 * This is only present if a [Species] has actual stat related differences from the "base".
 *
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Variant_Pok%C3%A9mon">Bulbapedias entry on variant Pokémon</a>
 */
class FormData internal constructor(
    private var baseSpeciesIdentifier: Identifier,
    private var battleOnly: Boolean
) {

    /**
     * Resolves the base species of this form.
     *
     * @return The [Species] that is the base of this form.
     */
    fun baseSpecies(): Species = CobblemonRegistries.SPECIES.get(this.baseSpeciesIdentifier)!!

    /**
     * Checks if this [FormData] can only exist in battles.
     *
     * @return True if this [FormData] can only exist in battles.
     */
    fun isBattleOnly(): Boolean = this.battleOnly

    companion object {

        val CODEC: Codec<FormData> = RecordCodecBuilder.create { builder ->
            builder.group(
                Identifier.CODEC.fieldOf("baseSpecies").forGetter(FormData::baseSpeciesIdentifier),
                Codec.BOOL.fieldOf("battleOnly").forGetter(FormData::battleOnly)
            ).apply(builder, ::FormData)
        }

    }

}