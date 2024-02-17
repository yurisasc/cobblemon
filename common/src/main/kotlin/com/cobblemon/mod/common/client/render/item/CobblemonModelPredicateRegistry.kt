/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.PokerodItem
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object CobblemonModelPredicateRegistry {

    fun registerPredicates() {
        ModelPredicateProviderRegistry.register(CobblemonItems.POKEROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })
    }
}