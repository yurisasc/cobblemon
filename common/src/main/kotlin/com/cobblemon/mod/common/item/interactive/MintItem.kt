/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.util.lang
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class MintItem(val nature: Nature) : PokemonInteractiveItem(Settings(), Ownership.OWNER) {
    override val sound = CobblemonSounds.MINT_USE
    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        consumeItem(player, stack)
        if ((entity.pokemon.mintedNature ?: entity.pokemon.nature) == nature) {
            player.sendMessage(lang("mint.same_nature", entity.pokemon.getDisplayName(), stack.name), true)
        } else {
            entity.pokemon.mintedNature = nature
            player.sendMessage(lang("mint.interact", entity.pokemon.getDisplayName(), stack.name), true)
        }
        return true
    }
}