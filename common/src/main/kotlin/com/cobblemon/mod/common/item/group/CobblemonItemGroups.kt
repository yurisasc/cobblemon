/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.group

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.item.CobblemonItems
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object CobblemonItemGroups {

    private val ALL = arrayListOf<ItemGroupProvider>()

    val BUILDING_BLOCKS = this.create("building_blocks") { ItemStack(CobblemonItems.APRICORN_PLANKS) }
    val MACHINES = this.create("machines") { ItemStack(CobblemonItems.HEALING_MACHINE) }
    val POKE_BALLS = this.create("pokeball") { ItemStack(CobblemonItems.POKE_BALL) }
    val EVOLUTION_ITEMS = this.create("evolution_item") { ItemStack(CobblemonItems.BLACK_AUGURITE) }
    val MEDICINE = this.create("medicine") { ItemStack(CobblemonItems.RARE_CANDY) }
    val HELD_ITEMS = this.create("held_item") { ItemStack(CobblemonItems.EXP_SHARE) }
    val PLANTS = this.create("plants") { ItemStack(CobblemonItems.RED_APRICORN) }

    fun register(consumer: (provider: ItemGroupProvider) -> ItemGroup) {
        ALL.forEach { provider -> provider.assign(consumer.invoke(provider)) }
    }

    private fun create(name: String, display: () -> ItemStack): ItemGroupProvider {
        val provider = CobblemonItemGroupProvider(cobblemonResource(name), Text.translatable("itemGroup.${Cobblemon.MODID}.${name}"), display)
        ALL += provider
        return provider
    }


}