/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.events.pokemon.HeldItemEvent
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeatureProvider
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatures
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object StashHandler {
    fun interactMob(player: PlayerEntity, pokemon: Pokemon, itemStack: ItemStack?): Boolean {
        if (itemStack == null || player !is ServerPlayerEntity || pokemon.getOwnerPlayer() !== player) return false
        val success = handleItem(pokemon, itemStack.item)
        if (success) {
            itemStack.decrement(1)
        }
        return success
    }

    fun giveHeldItem(event: HeldItemEvent.Post) {
        val pokemon = event.pokemon
        val item = event.received.item
        val success = handleItem(pokemon, item)
        if (success) {
            pokemon.removeHeldItem()
        }
    }

    fun handleItem(pokemon: Pokemon, item: Item): Boolean {
        val stashes = pokemon.species.stashes
        val itemIdentifier: Identifier = item.registryEntry.registryKey().value
        val stashName = stashes.entries.find { it.value.keys.contains(itemIdentifier) }?.key ?: return false
        val itemValue = stashes[stashName]?.get(itemIdentifier)
            ?: return false // This really shouldn't happen given how we get stashName

        val maxValue = (SpeciesFeatures.getFeature(stashName) as IntSpeciesFeatureProvider).max
        val pokeStash = pokemon.getFeature<IntSpeciesFeature>(stashName) ?: return false
        if (pokeStash.value >= maxValue) return false

        pokeStash.value += itemValue
        if (pokeStash.value > maxValue) pokeStash.value = maxValue

        if (pokemon.entity != null) pokemon.entity!!.playSound(CobblemonSounds.GIMMIGHOUL_GIVE_ITEM_SMALL, 1f, 1f)
        pokemon.markFeatureDirty(pokeStash)
        return true
    }
}