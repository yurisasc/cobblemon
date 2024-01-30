/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tags

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

/**
 * A collection of the Cobblemon [TagKey]s related to the [Registries.ITEM].
 *
 * @author Licious
 * @since January 8th, 2023
 */
@Suppress("unused", "HasPlatformType")
object CobblemonItemTags {

    @JvmField
    val APRICORN_LOGS = create("apricorn_logs")
    @JvmField
    val APRICORN_SEEDS = create("apricorn_seeds")
    @JvmField
    val APRICORNS = create("apricorns")
    /**
     * This tag is only used for a Torterra aspect based easter egg evolution at the moment.
     * It simply includes the 'minecraft:azalea' and 'minecraft:flowering_azalea' items by default.
     */
    @JvmField
    val AZALEA_TREE = create("azalea_tree")
    @JvmField
    val BERRIES = create("berries")
    @JvmField
    val BOATS = create("boats")
    @JvmField
    val COBBLEMON_SEEDS = create("cobblemon_seeds")
    @JvmField
    val EVOLUTION_ITEMS = create("evolution_items")
    @JvmField
    val EVOLUTION_STONES = create("evolution_stones")
    @JvmField
    val EXPERIENCE_CANDIES = create("experience_candies")
    @JvmField
    val HANGING_SIGNS = create("hanging_signs")
    @JvmField
    val HERBS = create("herbs")
    @JvmField
    val MINT_LEAF = create("mint_leaf")
    @JvmField
    val MINTS = create("mints")
    @JvmField
    val MUTATED_BERRIES = create("mutated_berries")
    @JvmField
    val PLANTS = create("plants")
    @JvmField
    val POKEBALLS = create("poke_balls")
    @JvmField
    val PROTEIN_INGREDIENTS = create("protein_ingredients")
    @JvmField
    val SEEDS = create("seeds")
    @JvmField
    val SIGNS = create("signs")
    @JvmField
    val ZINC_INGREDIENTS = create("zinc_ingredients")
    @JvmField
    val ANY_HELD_ITEM = create("held/is_held_item")
    @JvmField
    val EXPERIENCE_SHARE = create("held/experience_share")
    @JvmField
    val LUCKY_EGG = create("held/lucky_egg")
    @JvmField
    val DESTINY_KNOT = create("held/destiny_knot")
    @JvmField
    val EVERSTONE = create("held/everstone")
    @JvmField
    val POWER_ANKLET = create("held/power_anklet")
    @JvmField
    val POWER_BAND = create("held/power_band")
    @JvmField
    val POWER_BELT = create("held/power_belt")
    @JvmField
    val POWER_BRACER = create("held/power_bracer")
    @JvmField
    val POWER_LENS = create("held/power_lens")
    @JvmField
    val POWER_WEIGHT = create("held/power_weight")
    @JvmField
    val CONSUMED_IN_NPC_BATTLE = create("held/consumed_in_npc_battle")
    @JvmField
    val CONSUMED_IN_PVP_BATTLE = create("held/consumed_in_pvp_battle")
    @JvmField
    val CONSUMED_IN_WILD_BATTLE = create("held/consumed_in_wild_battle")

    /**
     * Tag that flags items as being able to "create" [CobblemonItems.LEFTOVERS].
     */
    @JvmField
    val LEAVES_LEFTOVERS = create("held/leaves_leftovers")

    private fun create(path: String) = TagKey.of(RegistryKeys.ITEM, cobblemonResource(path))

}