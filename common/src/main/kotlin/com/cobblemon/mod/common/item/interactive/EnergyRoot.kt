/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.block.EnergyRootBlock
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class EnergyRoot(block: EnergyRootBlock) : AliasedBlockItem(block, Settings()), InteractiveItem<PokemonEntity> {
    override fun onInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        TODO("Not yet implemented")
    }
}