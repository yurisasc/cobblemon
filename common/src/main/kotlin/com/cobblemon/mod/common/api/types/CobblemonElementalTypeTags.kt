/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.api.registry.CobblemonRegistryKeys
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.tag.TagKey

/**
 * Contains all the Cobblemon default elemental type related tags.
 */
object CobblemonElementalTypeTags {

    val FIRE_IMMUNE: TagKey<ElementalType> = TagKey.of(CobblemonRegistryKeys.ELEMENTAL_TYPE, cobblemonResource("fire_immune"))

    val FALL_DAMAGE_IMMUNE: TagKey<ElementalType> = TagKey.of(CobblemonRegistryKeys.ELEMENTAL_TYPE, cobblemonResource("fall_damage_immune"))

    val LURE_BALL_BOOSTING: TagKey<ElementalType> = TagKey.of(CobblemonRegistryKeys.ELEMENTAL_TYPE, cobblemonResource("lure_ball_boosting"))

    val NET_BALL_BOOSTING: TagKey<ElementalType> = TagKey.of(CobblemonRegistryKeys.ELEMENTAL_TYPE, cobblemonResource("net_ball_boosting"))

}