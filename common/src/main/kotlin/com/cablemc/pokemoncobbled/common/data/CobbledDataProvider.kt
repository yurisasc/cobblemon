package com.cablemc.pokemoncobbled.common.data

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.data.DataProvider
import com.cablemc.pokemoncobbled.common.api.data.DataRegistry
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader

internal object CobbledDataProvider : DataProvider {

    // Both Forge n Fabric keep insertion order so if a registry depends on another simply register it after
    var canReload = true
    fun registerDefaults() {
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
                canReload = false
            } else {
                LOGGER.info("Note: Pokémon Cobbled data registries skipped as Pokémon species is not safe to reload.")
            }
        }
    }
}