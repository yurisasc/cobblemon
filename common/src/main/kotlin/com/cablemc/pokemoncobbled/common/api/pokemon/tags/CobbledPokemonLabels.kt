/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokemon.tags

/**
 * A collection of commonly used labels in the mod.
 *
 * @author Licious
 * @since August 8th, 2022
 */
object CobbledPokemonLabels {

    /**
     * Represents a legendary Pokémon.
     */
    const val LEGENDARY = "legendary"

    /**
     * Represents a mythical Pokémon.
     * In Cobbled terms they do not exist since we do not share the concept of timed event only Pokémon but the official ones are still tagged.
     */
    const val MYTHICAL = "mythical"

    /**
     * Represents Pokémon that originate from Ultra Space.
     */
    const val ULTRA_BEAST = "ultra_beast"

    /**
     * Represents the pseudo legendary Pokémon.
     */
    const val PSEUDO_LEGENDARY = "pseudo_legendary"

    /**
     * Represents a Pokémon that has multiple forms depending on the region they're from.
     * In Cobbled/Minecraft terms there are no regions, but we follow the official concept.
     */
    const val REGIONAL = "regional"

}