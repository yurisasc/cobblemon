/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.advancement.criterion.CaughtPokemonCriterionCondition
import com.cobblemon.mod.common.advancement.criterion.PickStarterCriterionCondition
import com.cobblemon.mod.common.advancement.criterion.SimpleCountableCriterionCondition
import com.cobblemon.mod.common.advancement.criterion.SimpleCriterionTrigger
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.advancement.criterion.Criterion

/**
 * Contains all the advancement criteria in Cobblemon.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object CobblemonCriteria {
    val PICK_STARTER = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("pick_starter"),
            PickStarterCriterionCondition::class.java
        )
    )
    val CATCH_POKEMON = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("catch_pokemon"),
            CaughtPokemonCriterionCondition::class.java
        )
    )
    val CATCH_SHINY_POKEMON = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("catch_shiny_pokemon"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val EGG_HATCH = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("eggs_hatched"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val EVOLVE_POKEMON = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("pokemon_evolved"),
            SimpleCountableCriterionCondition::class.java
        )
    )
    val WIN_BATTLE = this.create(
        SimpleCriterionTrigger(
            cobblemonResource("battles_won"),
            SimpleCountableCriterionCondition::class.java
        )
    )

    private fun <T : Criterion<*>> create(criteria: T): T = Cobblemon.implementation.registerCriteria(criteria)

}