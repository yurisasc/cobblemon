/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.advancement.criterion.AspectCriterion
import com.cobblemon.mod.common.advancement.criterion.BattleCountableCriterion
import com.cobblemon.mod.common.advancement.criterion.CaughtPokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.CountableCriterion
import com.cobblemon.mod.common.advancement.criterion.EvolvePokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.LevelUpCriterion
import com.cobblemon.mod.common.advancement.criterion.PartyCheckCriterion
import com.cobblemon.mod.common.advancement.criterion.PlantTumblestoneCriterion
import com.cobblemon.mod.common.advancement.criterion.PokemonCriterion
import com.cobblemon.mod.common.advancement.criterion.PokemonInteractCriterion
import com.cobblemon.mod.common.advancement.criterion.SimpleCriterionTrigger
import com.cobblemon.mod.common.advancement.criterion.TradePokemonCriterion
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

/**
 * Contains all the advancement criteria in Cobblemon.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object CobblemonCriteria : PlatformRegistry<Registry<Criterion<*>>, RegistryKey<Registry<Criterion<*>>>, Criterion<*>>(){
    val PICK_STARTER = this.create("pick_starter", SimpleCriterionTrigger(PokemonCriterion.CODEC))

    val CATCH_POKEMON = this.create("catch_pokemon", SimpleCriterionTrigger(CaughtPokemonCriterion.CODEC))

    val CATCH_SHINY_POKEMON = this.create("catch_shiny_pokemon", SimpleCriterionTrigger(CountableCriterion.CODEC))

    val EGG_HATCH = this.create("eggs_hatched", SimpleCriterionTrigger(CountableCriterion.CODEC))

    val EVOLVE_POKEMON = this.create("pokemon_evolved", SimpleCriterionTrigger(EvolvePokemonCriterion.CODEC))

    val WIN_BATTLE = this.create("battles_won", SimpleCriterionTrigger(BattleCountableCriterion.CODEC))

    val DEFEAT_POKEMON = this.create("pokemon_defeated", SimpleCriterionTrigger(CountableCriterion.CODEC))

    val COLLECT_ASPECT = this.create("aspects_collected", SimpleCriterionTrigger(AspectCriterion.CODEC))

    val POKEMON_INTERACT = this.create("pokemon_interact", SimpleCriterionTrigger(PokemonInteractCriterion.CODEC))

    val PARTY_CHECK = this.create("party", SimpleCriterionTrigger(PartyCheckCriterion.CODEC))

    val LEVEL_UP = this.create("level_up", SimpleCriterionTrigger(LevelUpCriterion.CODEC))

    val PASTURE_USE = this.create("pasture_use", SimpleCriterionTrigger(PokemonCriterion.CODEC))

    val RESURRECT_POKEMON = this.create("resurrect_pokemon", SimpleCriterionTrigger(PokemonCriterion.CODEC))

    val TRADE_POKEMON = this.create("trade_pokemon", SimpleCriterionTrigger(TradePokemonCriterion.CODEC))

    // Advancement criteria for [grow_tumblestone.json]
    val PLANT_TUMBLESTONE = this.create("plant_tumblestone", SimpleCriterionTrigger(PlantTumblestoneCriterion.CODEC))

    override val registry = Registries.CRITERION
    override val registryKey = RegistryKeys.CRITERION
}