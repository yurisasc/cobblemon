/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import dev.architectury.registry.CreativeTabRegistry.create
import java.util.function.Supplier
import net.minecraft.item.ItemStack

object CobblemonCreativeTabs {
    val POKE_BALL_GROUP = create(cobblemonResource("pokeball"), Supplier { ItemStack(CobblemonItems.POKE_BALL.get()) })
    val EVOLUTION_ITEM_GROUP = create(cobblemonResource("evolution_item"), Supplier { ItemStack(CobblemonItems.BLACK_AUGURITE.get()) })
    val MEDICINE_ITEM_GROUP = create(cobblemonResource("medicine"), Supplier { ItemStack(CobblemonItems.RARE_CANDY.get()) })
    val HELD_ITEM_GROUP = create(cobblemonResource("held_item"), Supplier { ItemStack(CobblemonItems.EXP_SHARE.get()) })
    val PLANTS = create(cobblemonResource("plants"), Supplier { ItemStack(CobblemonItems.RED_APRICORN_SEED.get()) })
}