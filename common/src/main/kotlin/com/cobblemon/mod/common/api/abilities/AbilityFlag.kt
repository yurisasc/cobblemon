/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.abilities

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class AbilityFlag(private val showdownId: String) : StringRepresentable, ShowdownIdentifiable {

    /**
     * Can be suppressed by Mold Breaker and related effects
     */
    BREAKABLE("breakable"),

    /**
     * Ability can't be suppressed by e.g. Gastro Acid or Neutralizing Gas
     */
    CANT_SUPPRESS("cantsuppress"),

    /**
     * Role Play fails if target has this Ability
     */
    FAIL_ROLEPLAY("failroleplay"),

    /**
     * Skill Swap fails if either the user or target has this Ability
     */
    FAIL_SKILL_SWAP("failskillswap"),

    /**
     * Entrainment fails if user has this Ability
     */
    NO_ENTRAIN("noentrain"),

    /**
     * Receiver and Power of Alchemy will not activate if an ally faints with this Ability
     */
    NO_RECEIVER("noreceiver"),

    /**
     * Trace cannot copy this Ability
     */
    NO_TRACE("notrace"),

    /**
     * Disables the Ability if the user is Transformed
     */
    NO_TRANSFORM("notransform");

    override fun getSerializedName(): String = this.name

    override fun showdownId(): String = this.showdownId

    companion object {
        @JvmStatic
        val CODEC: Codec<AbilityFlag> = StringRepresentable.fromEnum(AbilityFlag::values)
    }

}