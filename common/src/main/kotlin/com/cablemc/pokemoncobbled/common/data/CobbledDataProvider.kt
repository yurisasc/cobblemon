package com.cablemc.pokemoncobbled.common.data

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.data.DataProvider
import com.cablemc.pokemoncobbled.common.api.data.DataRegistry
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader

internal object CobbledDataProvider : DataProvider {

    fun registerDefaults() {
        this.register(PokemonSpecies)
    }

    override fun register(registry: DataRegistry) {
        ReloadListenerRegistry.register(registry.type, SimpleResourceReloader(registry))
        PokemonCobbled.LOGGER.info("Registered the {} registry", registry.id.toString())
        PokemonCobbled.LOGGER.debug("Registered the {} registry of class {}", registry.id.toString(), registry::class.qualifiedName)
    }

    private class SimpleResourceReloader(private val registry: DataRegistry) : SynchronousResourceReloader {
        override fun reload(manager: ResourceManager) {
            this.registry.reload(manager)
        }
    }
}