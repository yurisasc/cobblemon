package com.cablemc.pokemoncobbled.common.data

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.data.DataProvider
import com.cablemc.pokemoncobbled.common.api.data.DataRegistry
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader

internal object CobbledDataProvider : DataProvider {

    // Both Forge n Fabric keep insertion order so if a registry depends on another simply register it after
    var canReload = true
    val headRegistry = Moves
    // Change this when you add more registries so that it's always the final registry
    val tailRegistry = PokemonSpecies

    fun registerDefaults() {
        this.register(Moves)
        this.register(PokemonSpecies)
    }

    override fun register(registry: DataRegistry) {
        ReloadListenerRegistry.register(registry.type, SimpleResourceReloader(registry))
        LOGGER.info("Registered the {} registry", registry.id.toString())
        LOGGER.debug("Registered the {} registry of class {}", registry.id.toString(), registry::class.qualifiedName)
    }

    private class SimpleResourceReloader(private val registry: DataRegistry) : SynchronousResourceReloader {
        override fun reload(manager: ResourceManager) {
            if (canReload) {
                this.registry.reload(manager)
                if (registry == tailRegistry) {
                    // Turn it off since this is the final registry. It can be turned back on upon client logout.
                    canReload = false
                }
            } else if (registry == headRegistry) { // Only send message once
                LOGGER.info("Note: Pokémon Cobbled data registries were skipped as Pokémon species are not safe to reload.")
            }
        }
    }
}