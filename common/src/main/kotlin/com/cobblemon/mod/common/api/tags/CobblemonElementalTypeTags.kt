/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tags

import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.tags.TagKey

@Suppress("unused")
object CobblemonElementalTypeTags {

    /**
     * Contains the elemental types present in the mains series games.
     */
    @JvmStatic
    val OFFICIAL = this.create("official")

    /**
     * Intended to be used by third party contains custom elemental types not present in the mains series games.
     * There is no guarantee third party authors will adhere to this principle.
     */
    @JvmStatic
    val CUSTOM = this.create("custom")

    /**
     * Types introduced in generation 1.
     */
    @JvmStatic
    val INTRODUCED_IN_GENERATION_1 = this.create("introduced_in_generation_1")

    /**
     * Types introduced in generation 6.
     */
    @JvmStatic
    val INTRODUCED_IN_GENERATION_6 = this.create("introduced_in_generation_6")

    /**
     * Types present here do not drown.
     */
    @JvmStatic
    val DROWN_IMMUNE = this.create("immunity/drown")

    /**
     * Types present here do not take damage from falling.
     */
    @JvmStatic
    val FALL_IMMUNE = this.create("immunity/fall")

    /**
     * Types present here do not take fire damage.
     */
    @JvmStatic
    val FIRE_IMMUNE = this.create("immunity/fire")

    /**
     * Types present here do not take lava damage.
     */
    @JvmStatic
    val LAVA_IMMUNE = this.create("immunity/lava")

    /**
     * Types present here do not take thunder damage.
     */
    @JvmStatic
    val THUNDER_IMMUNE = this.create("immunity/thunder")

    private fun create(path: String) = TagKey.create(CobblemonRegistries.ELEMENTAL_TYPE_KEY, cobblemonResource(path))

}