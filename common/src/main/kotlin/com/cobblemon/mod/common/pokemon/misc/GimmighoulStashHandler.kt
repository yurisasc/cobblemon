/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.misc

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.events.pokemon.interaction.HeldItemUpdatedEvent
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory

/**
 * Handles logic relating to Gimmighoul's Coin Stash and Scrap Stash bars.
 *
 * @author whatsy
 */
object GimmighoulStashHandler {

    val COIN_VALUE = 1
    val POUCH_VALUE = COIN_VALUE * 9
    val SACK_VALUE = POUCH_VALUE * 9

    val SCRAP_VALUE = 1
    val INGOT_VALUE = SCRAP_VALUE * 4
    val BLOCK_VALUE = INGOT_VALUE * 9

    fun giveHeldItem(event: HeldItemUpdatedEvent) {
        val player = event.cause ?: return
        val goldHoard = event.pokemon.getFeature<IntSpeciesFeature>("gimmighoul_coins")
        val netheriteHoard = event.pokemon.getFeature<IntSpeciesFeature>("gimmighoul_netherite")

        if (goldHoard != null && goldHoard.value < 999) {
            val increase = when (event.newItem.item) {
                CobblemonItems.RELIC_COIN -> COIN_VALUE
                CobblemonItems.RELIC_COIN_POUCH -> POUCH_VALUE
                CobblemonItems.RELIC_COIN_SACK -> SACK_VALUE
                else -> 0
            }

            if (increase != 0) {
                goldHoard.value += increase
                if (goldHoard.value > 999) goldHoard.value = 999
                if (event.decrement) event.originalStack.decrement(1)
                if (event.pokemon.entity != null) player.world.playSound(null, player.blockPos, CobblemonSounds.GIMMIGHOUL_GIVE_ITEM_SMALL, SoundCategory.PLAYERS)
                // Features need to be marked dirty to update the client + trigger storage saving
                event.pokemon.markFeatureDirty(goldHoard)
                event.cancel()
            }
        }

        if (netheriteHoard != null && netheriteHoard.value < 256 && event.newItem.isOf(Items.NETHERITE_SCRAP)) {
            val increase = when (event.newItem.item) {
                Items.NETHERITE_SCRAP -> SCRAP_VALUE
                Items.NETHERITE_INGOT -> INGOT_VALUE
                Items.NETHERITE_BLOCK -> BLOCK_VALUE
                else -> 0
            }
            netheriteHoard.value += increase
            if (netheriteHoard.value > 256) netheriteHoard.value = 256
            if (event.decrement) event.originalStack.decrement(1)
            if (event.pokemon.entity != null) player.world.playSound(null, player.blockPos, CobblemonSounds.GIMMIGHOUL_GIVE_ITEM_SMALL, SoundCategory.PLAYERS)
            // Features need to be marked dirty to update the client + trigger storage saving
            event.pokemon.markFeatureDirty(netheriteHoard)
            event.cancel()
        }
    }

}