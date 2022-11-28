/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.interactive.PokemonInteractiveItem
import com.cobblemon.mod.common.util.tooltipLang
import com.cobblemon.mod.common.world.block.BerryBlock
import net.minecraft.block.ComposterBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.world.World
import java.util.*

class BerryItem(private val berryBlock: BerryBlock) : AliasedBlockItem(berryBlock, Settings().group(CobblemonItemGroups.PLANTS)), PokemonInteractiveItem {

    init {
        // 65% to raise composter level
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE[this] = .65F
    }

    fun berry() = this.berryBlock.berry

    override val accepted: Set<PokemonInteractiveItem.Ownership> = EnumSet.of(PokemonInteractiveItem.Ownership.OWNER)

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip.add(tooltipLang(this.berry().identifier.namespace, this.berry().identifier.path).gray())
    }

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        // ToDo we need a system to handle interactions on a per berry impl level
        return false
    }

}