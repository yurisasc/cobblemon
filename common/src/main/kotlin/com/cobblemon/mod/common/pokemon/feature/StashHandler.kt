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
        val itemIdentifier: Identifier = item.registryEntry.registryKey().value
        val speciesFeatureProviders = SpeciesFeatures.getFeaturesFor(pokemon.species)
        val relevantSpeciesFeatureProviders: List<IntSpeciesFeatureProvider> = speciesFeatureProviders.filter {
            it is IntSpeciesFeatureProvider && it.itemPoints.keys.contains(itemIdentifier)
        }.map { it as IntSpeciesFeatureProvider }
        for (relevantSpeciesFeatureProvider in relevantSpeciesFeatureProviders) {
            val points = relevantSpeciesFeatureProvider.itemPoints[itemIdentifier]!!
            val feature = pokemon.getFeature<IntSpeciesFeature>(relevantSpeciesFeatureProvider.keys[0])!!
            feature.value += points
            pokemon.markFeatureDirty(feature)
            if (feature.value > relevantSpeciesFeatureProvider.max) feature.value = relevantSpeciesFeatureProvider.max
        }
        if (relevantSpeciesFeatureProviders.isNotEmpty()) {
            if (pokemon.entity != null) pokemon.entity!!.playSound(CobblemonSounds.GIMMIGHOUL_GIVE_ITEM_SMALL, 1f, 1f)
        }
        return relevantSpeciesFeatureProviders.isNotEmpty()
    }
}