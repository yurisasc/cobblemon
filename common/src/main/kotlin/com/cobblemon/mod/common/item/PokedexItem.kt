/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.net.messages.client.ui.PokedexUIPacket
import com.cobblemon.mod.common.util.isLookingAt
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World

class PokedexItem(val type: String) : CobblemonItem(Settings()) {

    override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.getStackInHand(usedHand)

        if (player !is ServerPlayerEntity) return TypedActionResult.success(itemStack, world.isClient)

        // Check if the player is interacting with a Pokémon
        val entity = player.world
            .getOtherEntities(player, Box.of(player.pos, 16.0, 16.0, 16.0))
            .filter { player.isLookingAt(it, stepDistance = 0.1F) }
            .minByOrNull { it.distanceTo(player) } as? PokemonEntity?

        if (!player.isSneaking) {
            if (entity != null) {
                val species = entity.pokemon.species.resourceIdentifier
                val form = entity.pokemon.form.formOnlyShowdownId()

                val pokedexData = Cobblemon.playerDataManager.getPokedexData(player)
                pokedexData.onPokemonSeen(species, form)
                player.sendPacket(SetClientPlayerDataPacket(PlayerInstancedDataStoreType.POKEDEX, pokedexData.toClientData(), false))
                PokedexUIPacket(type, species).sendToPlayer(player)
                player.playSoundToPlayer(CobblemonSounds.POKEDEX_SCAN, SoundCategory.PLAYERS, 1F, 1F)
            } else {
                PokedexUIPacket(type).sendToPlayer(player)
            }
            player.playSoundToPlayer(CobblemonSounds.POKEDEX_SHOW, SoundCategory.PLAYERS, 1F, 1F)
        }

        return TypedActionResult.success(itemStack, world.isClient)
    }
}