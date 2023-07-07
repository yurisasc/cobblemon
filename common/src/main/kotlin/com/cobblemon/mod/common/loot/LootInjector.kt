/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.loot

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus

/**
 * Used to append our loot table injections to existing ones.
 * This is not meant for API use and is only public due to visibility requirement in the platform implementations.
 *
 * @since June 8th, 2023
 */
@ApiStatus.Internal
object LootInjector {

    // cobblemon:injection/minecraft/chests/spawn_bonus_chest would target minecraft:chests/spawn_bonus_chest
    private const val PREFIX = "injection/"

    /**
     * Attempts to inject a Cobblemon injection loot table to a loot table being loaded.
     * This will automatically query the existence of an injection.
     *
     * @param id The [Identifier] of the loot table being loaded.
     * @param provider The job invoked if the injection is possible, this is what the platform needs to do to append the loot table.
     * @return If the injection was made.
     */
    fun attemptInjection(id: Identifier, lootManager: LootManager, provider: (LootPool.Builder) -> Unit): Boolean {
        // Don't attempt to do things with our own pools, don't remove the namespace check as other mods may use similar solutions
        if (id.namespace == Cobblemon.MODID && id.path.startsWith(PREFIX)) {
            return false
        }
        val resulting = this.convertToPotentialInjected(id)
        // Defaults to LootTable.EMPTY instead of null
        if (lootManager.getTable(resulting) != LootTable.EMPTY) {
            provider(this.injectLootPool(resulting))
            return true
        }
        return false
    }

    /**
     * Takes a source ID and converts it into the target injection.
     *
     * @param source The [Identifier] of the base loot table.
     * @return The [Identifier] for the expected Cobblemon injection.
     */
    private fun convertToPotentialInjected(source: Identifier): Identifier = cobblemonResource("$PREFIX${source.namespace}/${source.path}")

    /**
     * Creates a loot pool builder with our injection.
     *
     * @param resulting The [Identifier] for our injection table.
     * @return A [LootPool.Builder] with the [resulting] table.
     */
    private fun injectLootPool(resulting: Identifier): LootPool.Builder {
        return LootPool.builder()
            .with(LootTableEntry.builder(resulting).weight(1))
            .bonusRolls(UniformLootNumberProvider.create(0F, 1F))
    }

}