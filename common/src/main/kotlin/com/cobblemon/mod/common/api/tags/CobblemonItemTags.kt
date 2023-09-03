/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tags

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

/**
 * A collection of the Cobblemon [TagKey]s related to the [Registry.ITEM].
 *
 * @author Licious
 * @since January 8th, 2023
 */
object CobblemonItemTags {

    @JvmField
    val APRICORN_LOGS = create("apricorn_logs")
    @JvmField
    val APRICORN_SEEDS = create("apricorn_seeds")
    @JvmField
    val APRICORNS = create("apricorns")
    @JvmField
    val EXPERIENCE_CANDIES = create("experience_candies")
    @JvmField
    val POKEBALLS = create("poke_balls")
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
    val EVOLUTION_STONES = create("evolution_stones")
    @JvmField
    val EVOLUTION_ITEMS = create("evolution_items")
    @JvmField
    val MINTS = create("mints")
    @JvmField
    val MINT_LEAVES = create("mint_leaves")

    /**
     * This tag is only used for a Torterra aspect based easter egg evolution at the moment.
     * It simply includes the 'minecraft:azalea' and 'minecraft:flowering_azalea' items by default.
     */
    val AZALEA_TREE = create("azalea_tree")

    private fun create(path: String) = TagKey.of(RegistryKeys.ITEM, cobblemonResource(path))

}