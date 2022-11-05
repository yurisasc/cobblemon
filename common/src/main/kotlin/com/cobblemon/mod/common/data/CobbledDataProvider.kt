/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data

import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.data.DataProvider
import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.properties.PropertiesCompletionProvider
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

internal object CobblemonDataProvider : DataProvider {

    // Both Forge n Fabric keep insertion order so if a registry depends on another simply register it after
    var canReload = true
    private val registries = mutableListOf<DataRegistry>()

    fun registerDefaults() {
        this.register(Moves)
        this.register(Abilities)
        this.register(PokemonSpecies)
        this.register(PropertiesCompletionProvider)
    }

    override fun register(registry: DataRegistry) {
        // Only send message once
        if (this.registries.isEmpty()) {
            LOGGER.info("Note: Cobblemon data registries are only loaded once per server instance as Pokémon species are not safe to reload.")
        }
        ReloadListenerRegistry.register(registry.type, SimpleResourceReloader(registry))
        this.registries.add(registry)
        LOGGER.info("Registered the {} registry", registry.id.toString())
        LOGGER.debug("Registered the {} registry of class {}", registry.id.toString(), registry::class.qualifiedName)
    }

    override fun fromIdentifier(registryIdentifier: Identifier): DataRegistry? = this.registries.find { it.id == registryIdentifier }

    override fun sync(player: ServerPlayerEntity) {
        if (!player.networkHandler.connection.isLocal) {
            this.registries.forEach { registry -> registry.sync(player) }
        }
    }

    private class SimpleResourceReloader(private val registry: DataRegistry) : SynchronousResourceReloader {
        override fun reload(manager: ResourceManager) {
            if (canReload) {
                this.registry.reload(manager)
            }
        }
    }
}