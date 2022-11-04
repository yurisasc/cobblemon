/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.advancement

import com.cablemc.pokemod.common.advancement.criterion.*
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.advancement.criterion.Criteria

/**
 * Contains all the advancement criteria in Pokemod.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object PokemodCriteria {
    val PICK_STARTER = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("pick_starter"),
            PickStarterCriterionCondition::class.java
        )
    )
    val CATCH_POKEMON = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("catch_pokemon"),
            CaughtPokemonCriterionCondition::class.java
        )
    )
    val CATCH_SHINY_POKEMON = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("catch_shiny_pokemon"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val EGG_HATCH = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("eggs_hatched"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val EVOLVE_POKEMON = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("pokemon_evolved"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val WIN_BATTLE = Criteria.register(
        SimpleCriterionTrigger(
            pokemodResource("battles_won"),
            SimpleCountableCriterionCondition::class.java
        )
    )
}